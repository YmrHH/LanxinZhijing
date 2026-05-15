# 蓝心知径 Android 开发计划（PLAN）

> **强制规则**：此后每一次开发，必须先更新本文件，再写代码。不得先写代码后补计划。不得跳过本文件。不得擅自增删功能。不得跳阶段开发。
>
> 仓库：https://github.com/YmrHH/LanxinZhijing.git  
> 基线提交（参考）：`8727d87e8ab89b15e0f75203197fef097e28eb87`

---

## 1. 项目定位

**项目名称**：蓝心知径｜AI 知识树学习助手

**项目类型**：Android 原生应用（长期迭代为真实可用软件，非一次性 Demo）

**核心定位**：蓝心知径不是普通 AI 答题器，也不是普通文档总结器。

- 普通 AI：直接回答问题  
- 蓝心知径：识别学习内容 → 判断考点 → 分析卡点 → 拆解知识节点 → 形成个人知识树 → 节点追问 → 费曼复述 → 更新掌握度

**包名**：`com.lanxin.zhijing`  
**应用名**：蓝心知径  
**最低 SDK**：26

**固定页面（6 个，不得擅自增删）**：

| 页面 | 说明 |
|------|------|
| HomeScreen | 首页 / 学习入口 |
| AnalysisScreen | AI 学习分析 |
| KnowledgeTreeScreen | 个人知识树 |
| NodeFocusScreen | 节点聚焦对话 |
| ReviewScreen | 费曼复述与复习 |
| ProfileScreen | 我的（可简单占位） |

**底部导航（4 项，固定）**：首页 · 知识树 · 复习 · 我的

---

## 2. 长期开发原则

1. 严格按照本 PLAN 分版本、分阶段执行。
2. 每次开发前先读 PLAN，判断任务所属版本；PLAN 未记录则先更新 PLAN。
3. 不允许随意增加/删除页面与核心功能。
4. 不把项目改成普通聊天机器人、普通错题本或 Web App。
5. 先 Mock / 本地持久化打通流程，再接入 OCR、真实 AI、系统入口等重能力。
6. UI 保持简洁、可维护；MVVM：UI → ViewModel → Repository →（Room / AI 抽象）。
7. 需求不清时按 PLAN 已有内容实现，扩展须用户确认。
8. 代码保持组件化；密钥与隐私按本 PLAN 第十三节执行。

---

## 3. 技术栈固定要求

**必须使用**：

- Kotlin
- Jetpack Compose + Material 3
- Navigation Compose
- MVVM（ViewModel + Repository）
- Room + KSP（本地持久化，自 V0.2 起）
- Kotlin Coroutines + Flow
- `minSdk = 26`

**不得使用**（除非 PLAN 明确允许的新阶段）：

- React / Vue / Flutter / WebView 作为主界面
- 后台管理系统
- 客户端硬编码 AppKEY、直连生产蓝心大模型（V0.5 前）
- 除 PLAN 允许的 **ML Kit 设备端 OCR** 外，擅自接入其它 OCR / 文档解析云服务（禁止）
- 登录注册、云同步、会员、支付、课程商城、社区（未规划前禁止）

**依赖版本（当前）**：见 `gradle/libs.versions.toml`（Room 2.7.0 + KSP 2.0.21；CameraX；**ML Kit 中文 OCR**；`gradle.properties` 含 `android.disallowKotlinSourceSets=false` 以配合 KSP）。

---

## 4. 当前版本状态

### 4.1 当前版本状态：0.1.2（审计基线）

**0.1.2 已具备**：

1. Android 原生项目结构  
2. Kotlin + Jetpack Compose + Material3  
3. Navigation Compose  
4. 六个基础页面：Home / Analysis / KnowledgeTree / NodeFocus / Review / Profile  
5. 底部导航  
6. `picture/` 原型图目录（含 `tubiao.png`）  
7. **AI 抽象层**：`AiLearningRepository`、`MockAiLearningRepository`、`VivoLanxinAiRepository`（占位）、`AiLearningModels`、`AiLearningFixtures`、`ImportSource`  
8. Room 依赖（KSP 编译）  
9. **本地数据库**：Entity / DAO / `AppDatabase` / `LocalMappers` / `LearningRepository`  
10. 首页「最近学习」从 Room 读取（`learning_content`）  
11. 知识树节点与关系从 Room 读取  
12. 节点对话写入 `chat_message`  
13. 费曼复述写入 `review_record` + 掌握度 `mastery_record` / `knowledge_node`  
14. **V0.3 雏形**：粘贴文本导入、SAF 文件选择 UTF-8 文本导入（见第七节标注，**不作正式 V0.3 验收**）

**0.1.2 仍存在的问题（由 0.1.3 处理）**：

1. 本 PLAN 曾停留在「一次性 Demo」表述，已在本版重写为长期路线。  
2. V0.2 计划与代码不同步 → 0.1.3 对齐并验收。  
3. V0.3 仅有部分导入雏形，无系统分享、划词、拍照等 → 留待 V0.3 正式开发。  
4. `AndroidManifest.xml` 无系统分享文本/图片/文件、划词分析入口。  
5. 文件导入仅简单 UTF-8，不支持 PDF/DOCX/图片 OCR。  
6. `LanxinZhijingApplication` 使用 `runBlocking` 初始化，可能阻塞启动 → **0.1.3 修复**。  
7. `versionName` 曾为 `1.0`，未体现语义化版本 → **0.1.3 改为 0.1.3**。  
8. Room 持久化需完整人工验证：杀进程重启后数据仍在。  
9. 禁止现在接真实蓝心大模型、真实 OCR、登录、云同步、会员、社区、课程商城。

### 4.2 当前执行版本：0.1.10（PDF 后端解析契约 + 客户端调用，已完成）

**0.1.10 已完成**：`ExtractResult.Pdf`；`BackendPdfParse` POST `/api/lanxin/v1/parse-pdf`（`fileName` + `contentBase64`）；`PdfImportBodyResolver` 供 SAF/分享导入；未配置 `ai.backend.baseUrl` 时仍为占位正文；`versionName = 0.1.10`。

**0.1.9 已完成（归档）**：分析页加载/错误/重试。

**下一步**：复习页费曼联调加载与重试；V0.6 追问强化；服务端实现 `parse-pdf` 与蓝心衔接。

---

## 6. 当前阶段执行计划（0.1.10 · PDF 后端解析）

### 6.1 本次开发计划（0.1.10）

**任务目标**：§12.8 增加 `parse-pdf`；`DocumentImportHelper` 对 PDF 返回 `Pdf(bytes)`；`BackendPdfParse` + `PdfImportBodyResolver`；`LearningViewModel.stageFromFile` 与 `ImportIntentParser` 在配置后端时请求解析并填入预览正文。

**允许修改**：`PLAN.md`、`app/build.gradle.kts`、`data/importutil/*.kt`、`data/ai/BackendPdfParse.kt`（新）、`viewmodel/LearningViewModel.kt`

**禁止**：新主页面、改 Room Entity、在 PLAN 写密钥

**验收**：未配置后端时 PDF 仍为占位；配置且后端返回 `text` 时预览为解析正文；失败时占位 + 失败说明；`compileDebugKotlin` 通过（**已完成**）

---

## 6-legacy-019. 历史：0.1.9 · 分析页 §12.7（已完成）

<details>
<summary>0.1.9 范围（归档）</summary>

**任务目标**：`analysisLoading` / `analysisError`、`AnalysisScreen` 重试。

**验收**：`versionName = "0.1.9"`。

</details>

---

## 6-legacy-018. 历史：0.1.8 · 分步提示接 AI（已完成）

<details>
<summary>0.1.8 范围（归档）</summary>

**任务目标**：`buildNodeQuestionContext`；`generateStepHints` + 加载/重试；`askNodeQuestion` 同上下文；`versionName = 0.1.8`。

</details>

---

## 6-legacy-017. 历史：0.1.7 · V0.5 后端代理骨架（已完成）

<details>
<summary>0.1.7 范围（归档）</summary>

**任务目标**：`AiRepositoryFactory`、`BackendProxy`、`Fallback`、`BuildConfig`、`§12.8`、`INTERNET`、debug 明文、`ProfileScreen` AI 通道。

**验收**：`versionName = "0.1.7"`。

</details>

---

## 6-legacy-016. 历史：0.1.6 · V0.4 图片 OCR（已完成）

<details>
<summary>0.1.6 范围（归档）</summary>

**任务目标**：`ImageOcrHelper`（ML Kit Chinese）；`ImportIntentParser` / `stageFromFile` / `stageFromImageUri` 异步 OCR；移除 pdfbox。

**验收**：`versionName = "0.1.6"`；`assembleDebug` 通过。

</details>

---

## 6-legacy-015. 历史：0.1.5 · 多格式文档导入（已完成）

<details>
<summary>0.1.5 范围（归档）</summary>

**支持格式（当时）**：纯文本、~~pdf（本地）~~、docx、epub、odt、rtf、图片占位、doc 提示。

**验收**：`versionName = "0.1.5"`；单文件 10MB。

</details>

---

## 5. 版本路线图

路线图分两层：**语义版本**（0.1.x，工程整理与小步验收）与 **能力版本**（V0.x，产品能力阶段）。

### 5.1 能力版本（V0.x）

| 阶段 | 名称 | 目标 |
|------|------|------|
| **V0.1** | 可运行原型版 | 基础页面、底部导航、Mock 数据、Mock AI 流程、UI 还原 |
| **V0.2** | 本地数据持久化版 | Room：学习记录、知识节点、关系、对话、复述、掌握度可保存并在重启后保留 |
| **V0.3** | 真实导入能力版 | 拍照、文件选择、粘贴、相册截图、系统分享（文本/图片/文件）、划词分析入口；导入预览与修正（ImportPreviewScreen） |
| **V0.4** | OCR 与文档解析版 | 图片/截图/拍照 **ML Kit OCR**；TXT/DOCX/EPUB 等本地抽取；导入预览与用户修正；**PDF 正文以 V0.5 AI/后端为主（客户端占位+手改）** |
| **V0.5** | 真实 AI 学习分析版 | 接入 vivo 蓝心大模型或后端代理；**PDF 全文/扫描件等可由模型解析**；真实分析、知识点与关系生成 |
| **V0.6** | 节点追问可用版 | 节点上下文真实追问，保存对话历史 |
| **V0.7** | 费曼复述真实评分版 | AI 评分、更新掌握度、生成复习任务 |
| **V0.8** | 复习计划与提醒版 | 待复习列表、任务状态、本地提醒、WorkManager |
| **V0.9** | 稳定性与隐私优化版 | 错误处理、加载/空状态、权限与隐私说明、失败重试、大文件限制 |

### 5.2 语义版本（近期）

| 版本 | 对应能力 | 说明 |
|------|----------|------|
| 0.1.1 | V0.1 | 原型与 Mock AI 抽象 |
| 0.1.2 | V0.2 主体 + V0.3 雏形 | Room 与导入雏形并存 |
| **0.1.3** | V0.2 验收收尾 + 图标 | 已完成 |
| **0.1.4** | **正式 V0.3（第一批）** | **已完成** |
| **0.1.5** | **V0.4 多格式文档导入（第一批）** | **已完成** |
| **0.1.6** | **V0.4 图片 OCR；PDF 延后 V0.5 AI** | **已完成** |
| **0.1.7** | **V0.5 第一批：后端代理 + Fallback + BuildConfig** | **已完成** |
| **0.1.8** | **分步提示接 AI + Room 构建 NodeQuestionContext** | **已完成** |
| **0.1.9** | **分析页 AI 加载/失败/重试（§12.7）** | **已完成** |
| **0.1.10** | **PDF：`parse-pdf` 后端 + 导入预览** | **已完成** |

---

## 6-legacy-014. 历史：0.1.4 · 正式 V0.3（已完成）

<details>
<summary>0.1.4 范围（归档）</summary>

### 6.1 本次开发计划（0.1.4）

**任务目标**：完成 V0.3 第一批——导入预览页、CameraX 拍照、相册选图、系统分享（文本/图片/文件）、划词 `ACTION_PROCESS_TEXT`；导入后仍用 Mock AI 分析。

**允许修改**：

- `PLAN.md`
- `app/build.gradle.kts`、`gradle/libs.versions.toml`（CameraX、versionCode/Name）
- `AndroidManifest.xml`、`res/xml/file_paths.xml`
- `MainActivity.kt`
- `navigation/`、`viewmodel/LearningViewModel.kt`
- `data/importutil/`（意图解析、文本读取）
- `ui/screens/`（`ImportPreviewScreen`、`CameraCaptureScreen`；调整 `HomeScreen`）
- 不修改 Room Entity 结构（沿用 `import_record`）

**禁止修改**：

- 不接真实蓝心大模型 / OCR / PDF·DOCX 解析
- 不新增登录、云同步、会员、商城
- 不删除既有六主页面
- 不删除已有 V0.3 雏形逻辑（改为走预览页）

**新增页面（V0.3 允许）**：

- `ImportPreviewScreen`：导入预览与用户修正（非底部 Tab）
- `CameraCaptureScreen`：CameraX 拍照（非底部 Tab）

### 6.2 0.1.4 必须完成

1. `PendingImport` 暂存 → `ImportPreviewScreen` 编辑 → 确认落库 → `AnalysisScreen`  
2. 首页：拍错题 → CameraX；截图识别 → 相册选图；粘贴/文件 → 预览页  
3. `MainActivity`：`ACTION_SEND`（text/plain、image/*、*/*）与 `ACTION_PROCESS_TEXT`  
4. 图片导入占位文案（标明 V0.4 OCR），用户可手改后提交  
5. `versionName = "0.1.4"`，`versionCode = 3`  
6. `assembleDebug` 通过  

### 6.3 0.1.4 不允许做

1. 真实 OCR / PDF / DOCX  
2. 真实 vivo 蓝心 API  
3. 登录注册、云同步、会员、支付、社区  

### 6.4 0.1.4 验收标准

1. Gradle 编译通过  
2. 粘贴/选文件/选图/拍照/系统分享/划词均可进入导入预览  
3. 预览页可改标题与正文，确认后进入分析页且写入 Room  
4. 六主页面与底部导航正常  
5. 无 AppKEY、无真实 AI 网络请求  

</details>

---

## 6-legacy. 历史：0.1.3 执行计划（已完成）

<details>
<summary>0.1.3 范围（归档）</summary>

### 6.1 本次开发计划（0.1.3）

**允许修改的文件**：

- `PLAN.md`
- `app/build.gradle.kts`（仅 `versionCode` / `versionName`）
- `app/src/main/java/com/lanxin/zhijing/LanxinZhijingApplication.kt`
- `app/src/main/java/com/lanxin/zhijing/viewmodel/LearningViewModel.kt`（仅初始化相关）

**禁止修改**：

- 不新增页面（含 ImportPreviewScreen）
- 不修改 `AndroidManifest.xml` 增加分享/划词/相机权限
- 不接 `VivoLanxinAiRepository` 真实网络
- 不接 OCR / CameraX / PDF·DOCX 解析
- 不删除已有 V0.3 导入雏形代码
- 不重做 UI 视觉

**验收标准**：见第十一节「0.1.3 验收标准」。

### 6.2 0.1.3 只允许做

1. 更新 PLAN.md，建立长期开发计划（本章）。  
2. 记录「当前版本状态 0.1.2」与「当前执行 0.1.3」。  
3. 写入「每次开发必须先更新计划」强制流程。  
4. 核对 Room 表 / DAO / Repository 与 V0.2 要求一致（代码审查）。  
5. 验证：最近学习、知识树、对话、复述、掌握度从 Room 读写。  
6. 优化 App 初始化：移除 `Application` 中 `runBlocking`。  
7. `versionName = "0.1.3"`，`versionCode` 递增。  
8. 确认无 AppKEY、无真实 AI 请求。  
9. 不破坏现有 UI；不新增无关页面。

### 6.3 0.1.3 不允许做

1. 不接真实 vivo 蓝心大模型  
2. 不接真实 OCR  
3. 不做 CameraX 拍照  
4. 不做 PDF/DOCX 解析  
5. 不做系统分享入口  
6. 不做 `ACTION_PROCESS_TEXT`  
7. 不做登录注册、云同步、会员、支付、课程、社区、排行榜  
8. 不重构成其他技术栈  
9. 不删除已有页面  
10. 不改成普通聊天机器人  

</details>

---

## 7. 已完成内容

### V0.1（已完成）

- [x] 六页面 + 底部导航 + 跳转关系  
- [x] Mock 数据（`MockData`）  
- [x] `AiLearningRepository` + `MockAiLearningRepository`（默认）  
- [x] `VivoLanxinAiRepository` 占位（`Result.failure`，无网络）  
- [x] UI 主题与组件（`AppCard`、`PageHeader` 等）  

### V0.2（主体已完成，0.1.3 验收收尾）

- [x] Room：`learning_content`、`knowledge_node`、`knowledge_relation`、`chat_message`、`review_record`、`mastery_record`、`import_record`  
- [x] `LearningRepository` 统一封装数据库访问  
- [x] ViewModel 经 Repository 暴露 Flow / StateFlow  
- [x] UI 不直接访问 DAO  
- [x] 冷启动种子数据 `initializeIfNeeded()`（幂等）  
- [x] 费曼 mock 持久化 `ensureMockFeynmanPersisted()`（复习页触发）  
- [x] **0.1.3**：移除启动 `runBlocking`  
- [ ] **0.1.3**：杀进程重启人工验收清单（见第十一节，须真机确认）  

### V0.3 正式开发（0.1.4 已完成）

- [x] 雏形：粘贴、SAF 文本文件（升级为预览流）  
- [x] **0.1.4**：`ImportPreviewScreen`  
- [x] **0.1.4**：CameraX 拍照错题  
- [x] **0.1.4**：相册选截图  
- [x] **0.1.4**：系统分享 text/image/file  
- [x] **0.1.4**：划词 `ACTION_PROCESS_TEXT`  
- [x] V0.4：**0.1.5** docx/epub/odt/rtf/多编码 TXT 本地抽取  
- [x] V0.4：**0.1.6** 图片 ML Kit OCR；PDF 改 V0.5 AI 占位策略  
- [ ] V0.5：真实 AI + **PDF 由模型/后端解析**  
- [x] **0.1.8**：分步提示接 AI；Room 构建 `NodeQuestionContext`；节点追问同上下文  
- [x] **0.1.9**：分析页 AI 加载态 + 失败重试（§12.7）  
- [x] **0.1.10**：PDF 后端 `parse-pdf` + 导入预览（需 `ai.backend.baseUrl`）  

## 8. 未完成内容

| 项 | 计划阶段 |
|----|----------|
| PDF 全文（含扫描）由 AI/后端解析 | **V0.5** |
| 真实蓝心大模型 | V0.5 |
| 节点真实追问链路强化 | V0.6 |
| 费曼真实 AI 评分 | V0.7 |
| 复习计划与 WorkManager 提醒 | V0.8 |
| 全链路错误处理与隐私页 | V0.9 |
| 登录 / 云同步 / 会员 / 商城 | 未规划，禁止擅自开发 |

---

## 9. 禁止跳阶段开发的内容

在未更新 PLAN 并获用户确认前，**禁止**：

- V0.3 前：系统 Manifest 分享入口、划词、拍照、ImportPreviewScreen  
- V0.4 前：~~真实 OCR~~（**0.1.6 起允许设备端 ML Kit 图片 OCR**）；**不在此阶段强做客户端 PDF 全文库**  
- V0.5 前：客户端 AppKEY、直连蓝心生产 API、`VivoLanxinAiRepository` 真实请求  
- 任意阶段：登录、云同步、会员、支付、课程商城、社区  
- 删除六页面或改为非知识树主线产品  

**允许保留**：已存在的 V0.3 导入雏形代码，但不得当作 V0.3 已验收。

---

## 10. 每次开发前的强制流程

每次开发前**必须**：

1. 读取 `PLAN.md`。  
2. 判断用户要求属于哪个版本/阶段。  
3. 若 PLAN 未记录本次任务 → **先更新 PLAN**。  
4. 在 PLAN 中新增「本次开发计划」（允许改动的文件、禁止项、验收标准）。  
5. **然后**才允许写代码。  

---

## 11. 每次开发后的验收流程

每次开发后**必须**：

1. 更新 PLAN「开发记录」。  
2. 说明完成了哪些任务、修改了哪些文件。  
3. 说明是否影响已有功能。  
4. 说明是否通过自测（Gradle 构建、关键路径）。  
5. 给出下一步建议。  
6. 未完成须写明原因。  

### 11.1 0.1.3 验收标准

1. Gradle Sync / `assembleDebug` 通过  
2. App 正常启动（无 `runBlocking` 阻塞 `Application.onCreate`）  
3. 首页显示最近学习（种子或导入项）  
4. 杀进程重启后最近学习仍在  
5. 知识树：中心节点 + 周围节点 + 关系线  
6. 节点聚焦：历史对话可见；新发消息后重启仍在  
7. 复习页：费曼记录可保存；掌握度 42%→68% 后首页/知识树同步  
8. Profile 统计来自 DB 或占位不崩溃  
9. 无真实 AI 请求、无 OCR、无 AppKEY  
10. 无新增无关页面  
11. PLAN 已更新为长期计划并记录 0.1.3  
12. `versionName = "0.1.3"`  

---

## 12. AI 接入规范

### 12.1 架构原则

- UI 只依赖 ViewModel；ViewModel 依赖 `AiLearningRepository`。  
- **当前默认**：未配置 `ai.backend.baseUrl` 时为 `MockAiLearningRepository`；配置后为 `BackendProxyAiLearningRepository`（可包 `FallbackAiLearningRepository`）。  
- **V0.5**：服务端持有 AppKEY；客户端仅调自有后端（见 §12.8）。  
- 文档入口：https://aigc.vivo.com.cn/#/document/index?id=1746  

### 12.2 接口定义

```kotlin
interface AiLearningRepository {
    suspend fun analyzeLearningContent(text: String, sourceType: ImportSource): Result<LearningAnalysisResult>
    suspend fun askNodeQuestion(nodeContext: NodeQuestionContext, question: String): Result<String>
    suspend fun generateStepHints(nodeContext: NodeQuestionContext): Result<List<String>>
    suspend fun evaluateFeynmanAnswer(request: FeynmanEvaluationRequest): Result<FeynmanEvaluationResult>
}
```

### 12.3 数据模型

见 `AiLearningModels.kt`：`LearningAnalysisResult`、`NodeQuestionContext`、`FeynmanEvaluationRequest`、`FeynmanEvaluationResult`。

### 12.4 蓝心输出与 Prompt 原则

- 模型输出须为**严格 JSON**（字段名固定），禁止散文/Markdown 表格。  
- 学习分析 Prompt：识别内容类型、考点、关联点、卡点、学习路径、节点与关系（不直接给完整答案）。  
- 节点追问：围绕当前节点与掌握度，非泛聊天。  
- 分步提示：3–5 条，由浅入深，不一次性给完整答案。  
- 费曼评分：返回 score、level、strengths、weaknesses、suggestions、masteryBefore/After、nextTasks。  

详细 Prompt 条文保留在代码库 `VivoLanxinAiRepository` 注释与历史 PLAN 备份逻辑中；实现 V0.5 时以官方文档 + 后端为准。

### 12.5 文档与 PDF 策略

- **图片文字**：V0.4 起使用设备端 **ML Kit 中文识别**（离线模型由 Play 服务分发）。  
- **PDF**：不在客户端强依赖本地 PDF 库做全文抽取；**已配置 `ai.backend.baseUrl` 时**客户端 `POST /api/lanxin/v1/parse-pdf` 将 PDF Base64 交自有后端解析（见 §12.8）；未配置时导入预览仍为占位，可手改或导出 Word。  

### 12.6 AI 接入阶段

| 阶段 | AI 行为 |
|------|---------|
| V0.1 | 仅 Mock |
| V0.2 | Room 存学习与复述结构；AI 仍 Mock |
| V0.3 | 真实导入；AI 可 Mock（可基于文本摘要） |
| V0.4 | OCR/抽取后传入分析链；AI 可 Mock 或 Debug |
| V0.5+ | 正式蓝心或后端代理 |

### 12.7 错误处理（V0.5 起）

网络失败、鉴权失败、限流、空响应、JSON 解析失败、超时 → UI 展示加载中 / 失败 / 重试（可选 Mock 降级）。**已实现**：`AnalysisScreen`（`analysisLoading` / `analysisError`、「重新分析」）；节点聚焦分步提示（0.1.8）。

### 12.8 自有后端 JSON 契约（0.1.7 起，相对 `AI_BACKEND_BASE_URL`）

**Base**：无尾部 `/`。以下路径均为追加。

| 能力 | 方法 | 路径 | 请求 JSON | 响应 JSON |
|------|------|------|-----------|-----------|
| PDF 解析（Base64 → 正文） | POST | `/api/lanxin/v1/parse-pdf` | `fileName`（string）、`contentBase64`（string，PDF 原始字节 Base64） | `text`（string），或 `data.text` |
| 学习分析 | POST | `/api/lanxin/v1/analyze` | `text`（string）、`importSource`（string，与 `ImportSource.name` 一致） | 见下「分析结果」 |
| 节点追问 | POST | `/api/lanxin/v1/node-ask` | `nodeContext`（对象，字段与 `NodeQuestionContext` 一致；`relatedNodes` 为 `{id,label,progress,type}` 数组）、`question`（string） | `answer`（string） |
| 分步提示 | POST | `/api/lanxin/v1/step-hints` | 同 `nodeContext` 对象（与追问一致） | `hints`（string 数组） |
| 费曼评分 | POST | `/api/lanxin/v1/feynman-evaluate` | `nodeId`、`nodeTitle`、`question`、`userAnswer`、`masteryBefore` | `score`、`level`、`strengths`、`weaknesses`、`suggestions`、`masteryBefore`、`masteryAfter`、`nextTasks`（数组字段均为 JSON array） |

**分析结果**（与 `LearningAnalysisResult` 字段一致）：`contentType`、`coreTopic`、`relatedKnowledgePoints`（array of string）、`possibleWeakness`、`suggestedPath`（array of string）、`nodes`（`id,label,progress,type`）、`relations`（`from,relation,to`）。

鉴权、蓝心 AppKEY、PDF 上传解析均在**服务端**实现；客户端仅携带业务 JSON（及后续你可扩展的 `Authorization` header，不由本仓库写入密钥）。

**local.properties 示例（勿提交密钥）**：

```properties
ai.backend.baseUrl=http://10.0.2.2:8080
# 可选，默认 true：远端失败时使用 MockAiLearningRepository
ai.backend.fallbackToMock=true
```

---

## 13. 隐私与密钥安全规范

1. **禁止**将 AppKEY 写入：Kotlin 源码、`build.gradle`、`AndroidManifest.xml`、PLAN、README、Git 仓库。  
2. **禁止** Logcat / 报错信息打印 AppKEY。  
3. `local.properties` 必须在 `.gitignore`（已配置）。  
4. Debug 临时直连（若用户明确要求）：仅 `local.properties` + BuildConfig，仅 Debug，Release 禁止携带 AppKEY。  
5. 生产：Android → 自有后端 → vivo API（AppID/AppKEY 仅服务端）。  

---

## 14. UI 参考图规范

- 原型资源目录：`picture/`（当前含 `tubiao.png`；完整页面参考图按产品补充）。  
- 视觉：白底、圆角卡片、蓝色主色、浅绿提示卡、底部四 Tab。  
- 页面与跳转（固定）：

```
启动 → HomeScreen
Home → Analysis（拍错题/截图识别/导入成功后）
Home → KnowledgeTree（导数与单调性）
Analysis → KnowledgeTree / NodeFocus
KnowledgeTree → NodeFocus
NodeFocus → Review
```

- 各页必备文案与 Mock 主线以「导数与单调性」为演示核心；实现时以现有 `ui/screens/` 与 `ui/components/` 为准，不擅自改版式。

---

## 15. 当前开发任务追踪（0.1.10）

| # | 任务 | 状态 |
|---|------|------|
| 1 | PLAN：§4.2 / §6 / §12.5 / §12.8 / §5.2 / §15 | 已完成 |
| 2 | `ExtractResult.Pdf` + `DocumentImportHelper` | 已完成 |
| 3 | `BackendPdfParse` + `PdfImportBodyResolver` | 已完成 |
| 4 | `LearningViewModel` / `ImportIntentParser` 接入 | 已完成 |
| 5 | `versionName=0.1.10`，`versionCode=9` | 已完成 |

---

## 15-legacy-014tasks. 0.1.4 开发任务（已完成）

| # | 任务 | 状态 |
|---|------|------|
| 1 | 重写 PLAN 为长期结构（本章 1–15） | 已完成 |
| 2 | 记录 0.1.2 审计与 0.1.3 范围 | 已完成 |
| 3 | 审查 Room / Repository / ViewModel 分层 | 已完成（UI 无直连 DAO；ViewModel 经 Repository） |
| 4 | 移除 `Application.runBlocking`，改 ViewModel 异步 `initializeIfNeeded()` | 已完成 |
| 5 | `versionName=0.1.3`，`versionCode=2` | 已完成 |
| 6 | `assembleDebug` 自测 | 已通过 |
| 7 | 确认无 AppKEY / 无真实 AI 网络 | 已确认 |

**V0.2 Room 结构核对（代码审查结论）**：

| 表 | Entity | DAO | Repository 使用 |
|----|--------|-----|-----------------|
| learning_content | ✓ | LearningContentDao | learningItems, seed, import |
| knowledge_node | ✓ | KnowledgeNodeDao | 知识树、掌握度 |
| knowledge_relation | ✓ | KnowledgeRelationDao | 关系与边 |
| chat_message | ✓ | ChatMessageDao | 节点对话 |
| review_record | ✓ | ReviewRecordDao | 费曼复述 |
| mastery_record | ✓ | MasteryRecordDao | 掌握度变更记录 |
| import_record | ✓ | ImportRecordDao | V0.3 雏形导入 |

---

## 开发记录

### 2026-05-15 · 0.1.3

**计划**：重写 PLAN；V0.2 验收收尾；修复 `runBlocking`；版本号 0.1.3。

**已完成**：

1. PLAN 重写为 15 章长期结构，含 0.1.2 审计、V0.1–V0.9 路线图、0.1.3 范围与强制流程。  
2. 移除 `LanxinZhijingApplication` 中 `runBlocking`；在 `LearningViewModel.init` 中异步调用 `initializeIfNeeded()`（幂等）。  
3. `versionName = "0.1.3"`，`versionCode = 2`。  
4. 代码审查：7 张 Room 表齐全；`LearningRepository` 统一封装；UI 无直连 DAO；默认 `MockAiLearningRepository`；`VivoLanxinAiRepository` 仅占位无网络。  
5. `assembleDebug` 构建成功。

**修改文件**：

- `PLAN.md`  
- `app/build.gradle.kts`  
- `app/src/main/java/com/lanxin/zhijing/LanxinZhijingApplication.kt`  
- `app/src/main/java/com/lanxin/zhijing/viewmodel/LearningViewModel.kt`  

**影响**：启动不再阻塞主线程；种子数据在 ViewModel 创建后后台写入，首页 `learningItems` 为空时仍显示 `CircularProgressIndicator`，写入后 Flow 自动刷新。

**V0.2 持久化验收（代码路径 + 构建）**：

| 项 | 结论 |
|----|------|
| 最近学习 | `LearningContentDao` → `learningItems` Flow → HomeScreen |
| 知识树节点/关系 | `KnowledgeNodeDao` / `KnowledgeRelationDao` → KnowledgeTreeScreen |
| 节点对话 | `ChatMessageDao.addChatMessage` → `getChatMessages` Flow |
| 费曼复述 | `ReviewRecordDao` + `saveFeynmanReview`；复习页 `ensureMockFeynmanPersisted` |
| 掌握度同步 | `saveFeynmanReview` 更新 `knowledge_node` + 导数项 `learning_content` meta |
| 杀进程重启 | **须在真机/模拟器人工验证**（Room 文件 `lanxin_zhijing.db` 持久化设计已就绪） |

**自测**：Gradle `assembleDebug` 通过；无 AppKEY；无 HTTP 客户端依赖；`local.properties` 在 `.gitignore`。

**下一步**：真机完成第十一节杀进程清单后，可更新 PLAN 启动 **正式 V0.3**（须先写 PLAN：系统分享、划词、拍照、ImportPreviewScreen 等）。

---

## 本次开发任务：添加 APP 图标

**任务目标**：使用 `picture/tubiao.png` 作为蓝心知径 Android APP 的启动图标。

**本次允许修改**：

1. `PLAN.md`
2. `app/src/main/AndroidManifest.xml`
3. `app/src/main/res/drawable-nodpi/`（新增 `tubiao.png`）
4. `app/src/main/res/mipmap-anydpi-v26/`（仅当需要时；本次采用做法 A，不修改 adaptive icon）

**本次不允许修改**：

1. 不修改页面功能、AI、Room、Repository、ViewModel 业务逻辑  
2. 不新增页面；不接真实 AI / OCR；不做登录注册  
3. **不改动** `picture/tubiao.png` 原图  

**实施方案（做法 A）**：

- 复制 `picture/tubiao.png` → `res/drawable-nodpi/tubiao.png`  
- `AndroidManifest.xml`：`android:icon="@drawable/tubiao"`，`android:roundIcon="@drawable/tubiao"`  
- 保留现有 `mipmap-anydpi/ic_launcher*.xml`，不删除  

**验收标准**：

1. Gradle / `assembleDebug` 通过  
2. 桌面与多任务页显示 `tubiao` 图标  
3. Manifest 正确引用 `@drawable/tubiao`  
4. 不影响现有页面与功能  
5. 资源名为小写英文，无中文资源名  

### 2026-05-15 · 添加 APP 图标

**状态**：已完成  

**已完成**：

1. 复制 `picture/tubiao.png` → `app/src/main/res/drawable-nodpi/tubiao.png`（未修改原图）。  
2. `AndroidManifest.xml`：`android:icon="@drawable/tubiao"`，`android:roundIcon="@drawable/tubiao"`。  
3. 保留现有 `mipmap-anydpi/ic_launcher*.xml`，未删除。  
4. `assembleDebug` 构建成功。  

**修改文件**：

- `PLAN.md`  
- `app/src/main/AndroidManifest.xml`  
- `app/src/main/res/drawable-nodpi/tubiao.png`（新增）  

**影响**：仅更换启动图标资源引用；未改动页面、AI、Room、Repository、ViewModel。  

**自测**：Gradle 编译通过；桌面/多任务图标显示须在真机或模拟器安装后人工确认。  

---

### 2026-05-15 · 0.1.4 正式 V0.3（第一批）

**状态**：已完成  

**已完成**：

1. `PendingImport` 暂存 → `ImportPreviewScreen` 编辑 → 确认落库 → `AnalysisScreen`  
2. CameraX `CameraCaptureScreen`；相册 `image/*`；粘贴/文件经预览页  
3. `ACTION_SEND`（text/image/*）与 `ACTION_PROCESS_TEXT`；`MainActivity` + `ImportIntentParser`  
4. 图片导入占位文案（V0.4 OCR 提示）  
5. `versionName = "0.1.4"`，`versionCode = 3`；`assembleDebug` 通过  

**修改/新增文件**：

- `PLAN.md`  
- `gradle/libs.versions.toml`、`app/build.gradle.kts`  
- `AndroidManifest.xml`、`res/xml/file_paths.xml`  
- `MainActivity.kt`、`navigation/Routes.kt`、`navigation/AppNavGraph.kt`  
- `viewmodel/LearningViewModel.kt`  
- `data/importutil/PendingImport.kt`、`ImportIntentParser.kt`、`TextImportHelper.kt`  
- `ui/screens/ImportPreviewScreen.kt`、`CameraCaptureScreen.kt`、`HomeScreen.kt`  

**影响**：导入链路统一经预览页；六主页面与底部导航不变；仍用 Mock AI；无 OCR/真实蓝心。  

**自测**：编译通过；分享/划词/拍照/选图须在真机验证。  

**下一步**：**V0.5** 接入真实 AI / 后端；PDF 由模型解析；`AiLearningRepository` 真实实现。  

---

### 2026-05-15 · 0.1.10 PDF 后端 parse-pdf 与导入预览

**状态**：已完成  

**已完成**：

1. §12.8 新增 `POST /api/lanxin/v1/parse-pdf`（`fileName`、`contentBase64` → `text` 或 `data.text`）。  
2. `DocumentImportHelper.ExtractResult.Pdf`；`pdfPlaceholderBody`；`BackendPdfParse`；`PdfImportBodyResolver`。  
3. `LearningViewModel.stageFromFile`、`ImportIntentParser` 分享文件路径在配置 `ai.backend.baseUrl` 时请求解析。  
4. `versionName = "0.1.10"`，`versionCode = 9`。  

**修改文件**：`PLAN.md`、`app/build.gradle.kts`、`DocumentImportHelper.kt`、`ImportIntentParser.kt`、`LearningViewModel.kt`、`BackendPdfParse.kt`（新）、`PdfImportBodyResolver.kt`（新）  

**自测**：`compileDebugKotlin`。  

**下一步**：服务端实现 `parse-pdf`（调蓝心/解析库）；复习页费曼加载与重试。  

---

### 2026-05-15 · 0.1.9 分析页 AI 加载与失败重试

**状态**：已完成  

**已完成**：

1. `LearningViewModel`：`analysisLoading`、`analysisError`、`runLearningAnalysis`；`confirmPendingImport` 与 `refreshAnalysisForDisplay` 共用；`retryAnalysisForDisplay()`。  
2. `AnalysisScreen`：`LinearProgressIndicator`、错误卡与「重新分析」；加载中隐藏正文与按钮。  
3. `versionName = "0.1.9"`，`versionCode = 8`。  

**修改文件**：`PLAN.md`、`app/build.gradle.kts`、`LearningViewModel.kt`、`AnalysisScreen.kt`  

**自测**：`compileDebugKotlin`。  

**下一步**：PDF 解析链路或服务端接口；复习页/费曼联调加载与重试。  

---

### 2026-05-15 · 0.1.8 分步提示接 AI 与 Room 节点上下文

**状态**：已完成  

**已完成**：

1. `LearningRepository.buildNodeQuestionContext(nodeId)`：当前节点描述 + 知识树边邻居（至多 6 个）。  
2. `LearningViewModel`：`nodeStepHints` / `nodeStepHintsLoading` / `nodeStepHintsError`、`retryNodeStepHints()`；展开分步提示时请求 `generateStepHints`；切换焦点节点时清空并收起提示区。  
3. `NodeFocusScreen`：加载圈、错误文案与「重试」、动态条目列表。  
4. `sendUserMessageAndMockReply` 改用同一 `buildNodeQuestionContext`（不再使用 `demoNodeQuestionContext()`）。  
5. `versionName = "0.1.8"`，`versionCode = 7`。  

**修改文件**：`PLAN.md`、`app/build.gradle.kts`、`LearningRepository.kt`、`LearningViewModel.kt`、`NodeFocusScreen.kt`  

**自测**：`compileDebugKotlin`（由代理执行）。  

**下一步**：分析页分析中的加载与失败重试；服务端/模型侧 PDF 解析。  

---

### 2026-05-15 · 0.1.7 V0.5 后端代理骨架

**状态**：已完成  

**已完成**：

1. `local.properties` → `BuildConfig.AI_BACKEND_BASE_URL`、`AI_BACKEND_FALLBACK_TO_MOCK`（无密钥入仓）。  
2. `BackendProxyAiLearningRepository`（HttpURLConnection + §12.8 路径）；`AiBackendWireFormat` 严格 JSON；`FallbackAiLearningRepository`；`AiRepositoryFactory`。  
3. `LanxinZhijingApplication.aiLearningRepository` + `LearningViewModelFactory` 注入。  
4. `INTERNET`；`src/debug/AndroidManifest.xml` 明文 HTTP 便于 `10.0.2.2` 联调。  
5. `ProfileScreen` 展示 AI 通道摘要。  
6. `versionName = "0.1.7"`，`versionCode = 6`。  

**修改/新增文件**：`PLAN.md`、`app/build.gradle.kts`、`AndroidManifest.xml`、`app/src/debug/AndroidManifest.xml`、`LanxinZhijingApplication.kt`、`LearningViewModel.kt`、`ProfileScreen.kt`、`AiBackendWireFormat.kt`、`BackendProxyAiLearningRepository.kt`、`FallbackAiLearningRepository.kt`、`AiRepositoryFactory.kt`、`VivoLanxinAiRepository.kt`（注释）  

**自测**：`:app:compileDebugKotlin` 已通过；完整 `assembleDebug` 在 CI 环境因 `compileDebugJavaWithJavac` / Gradle Worker `ClassNotFoundException: GradleWorkerMain` 未跑通（Gradle/JVM 环境问题）；请在本地 Android Studio 或修复 Gradle Worker 后重跑 `assembleDebug`。  

**下一步**：实现服务端转发蓝心；**PDF** 上传/URL 解析接口；客户端可选 `Authorization`（仍不入仓密钥）。  

---

### 2026-05-15 · 0.1.6 图片 OCR 与 PDF 策略调整

**状态**：已完成  

**已完成**：

1. 移除 **pdfbox-android**；PDF 导入展示「待 V0.5 AI 解析」说明，用户可粘贴或导出 Word/文本。  
2. 新增 **ML Kit 中文文字识别**（`ImageOcrHelper`）：相册、拍照、分享图片、文件选择器选图经 OCR 填入预览；失败保留占位。  
3. `ImportIntentParser.parse` 改为 `suspend`，分享图片异步 OCR。  
4. `versionName = "0.1.6"`，`versionCode = 5`；`assembleDebug` 通过。  

**修改文件**：`PLAN.md`、`libs.versions.toml`、`app/build.gradle.kts`、`DocumentImportHelper.kt`、`ImageOcrHelper.kt`（新）、`ImportIntentParser.kt`、`LearningViewModel.kt`、`TextImportHelper.kt`  

---

### 2026-05-15 · 0.1.5 多格式文档导入

**状态**：已完成  

**已完成**：

1. 新增 `DocumentImportHelper` 统一按扩展名/MIME 抽取正文。  
2. 支持：txt/md/csv/json/xml/html 等纯文本（UTF-8/GBK）、~~pdf（pdfbox）~~、docx、epub、odt、rtf（**0.1.6 起 pdf 改 AI 占位、移除 pdfbox**）。  
3. **0.1.5 时**图片为占位；**0.1.6** 起相册/拍照/分享图走 ML Kit OCR。旧版 .doc 提示另存为 docx/pdf；失败格式可在预览页手改。  
4. 单文件上限 10MB；`versionName = "0.1.5"`，`versionCode = 4`；`assembleDebug` 通过。  

**修改文件**：`PLAN.md`、`libs.versions.toml`、`app/build.gradle.kts`、`DocumentImportHelper.kt`、`TextImportHelper.kt`、`ImportIntentParser.kt`、`LearningViewModel.kt`、`HomeScreen.kt`  

---

*本文档为蓝心知径唯一权威开发范围说明。与代码冲突时，以用户最新指令 + 更新后的 PLAN 为准。*
