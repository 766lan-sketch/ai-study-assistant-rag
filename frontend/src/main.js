import { createApp, onMounted, ref } from 'vue'
import './style.css'

const API = 'http://localhost:18080/api/study'

const App = {
  setup() {
    const tasks = ref([])
    const question = ref('')
    const loading = ref(false)
    const messages = ref([{ role: 'assistant', text: '你好，我已经接入本地 Java 知识库。可以问我：Controller 和 Service 有什么区别？', sources: [] }])

    onMounted(async () => {
      try {
        const [taskResponse, historyResponse] = await Promise.all([
          fetch(`${API}/tasks`),
          fetch(`${API}/history`)
        ])
        tasks.value = await taskResponse.json()
        const history = await historyResponse.json()
        for (const item of history) {
          messages.value.push({ role: 'user', text: item.question })
          messages.value.push({ role: 'assistant', text: item.answer, sources: item.sources || [] })
        }
      } catch {
        messages.value.push({ role: 'assistant', text: '后端还没有启动，请先启动 Java 后端。' })
      }
    })

    async function ask() {
      const text = question.value.trim()
      if (!text || loading.value) return
      messages.value.push({ role: 'user', text })
      question.value = ''
      loading.value = true
      try {
        const response = await fetch(`${API}/chat`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ question: text })
        })
        const data = await response.json()
        if (!response.ok) throw new Error(data.message || '请求失败')
        messages.value.push({ role: 'assistant', text: data.answer, sources: data.sources || [] })
      } catch (error) {
        messages.value.push({ role: 'assistant', text: `连接失败：${error.message}` })
      } finally {
        loading.value = false
      }
    }

    async function clearHistory() {
      if (!window.confirm('确定清空全部聊天记录吗？')) return
      try {
        const response = await fetch(`${API}/history`, { method: 'DELETE' })
        if (!response.ok) throw new Error('清空失败')
        messages.value = [{ role: 'assistant', text: '聊天记录已清空。可以开始新的学习对话。', sources: [] }]
      } catch (error) {
        messages.value.push({ role: 'assistant', text: `操作失败：${error.message}` })
      }
    }

    return { tasks, question, loading, messages, ask, clearHistory }
  },
  template: `
    <div class="shell">
      <header>
        <div><p class="eyebrow">JAVA + AI · DAY 1</p><h1>智能学习助手</h1><p class="subtitle">把每天要学的知识，变成可以运行的项目。</p></div>
        <span class="badge">RAG 已启用</span>
      </header>
      <div class="grid">
        <aside class="panel tasks">
          <h2>今日任务</h2>
          <label v-for="task in tasks" :key="task.id" class="task">
            <input type="checkbox"><span><strong>{{ task.title }}</strong><small>{{ task.description }}</small></span>
          </label>
          <p v-if="!tasks.length" class="muted">等待后端返回任务……</p>
        </aside>
        <main class="panel chat">
          <div class="chat-toolbar"><strong>学习对话</strong><button class="clear" type="button" @click="clearHistory">清空记录</button></div>
          <div class="messages">
            <div v-for="(message, index) in messages" :key="index" :class="['message', message.role]">
              <div>{{ message.text }}</div>
              <div v-if="message.sources?.length" class="sources">
                <strong>参考知识库</strong>
                <details v-for="source in message.sources" :key="source.title">
                  <summary>{{ source.title }}</summary>
                  <p>{{ source.excerpt }}</p>
                </details>
              </div>
            </div>
            <div v-if="loading" class="message assistant">正在思考……</div>
          </div>
          <form @submit.prevent="ask">
            <input v-model="question" maxlength="500" placeholder="输入问题，例如：Service 是什么？">
            <button :disabled="loading">发送</button>
          </form>
        </main>
      </div>
    </div>
  `
}

createApp(App).mount('#app')
