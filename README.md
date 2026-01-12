# VoiceWaveView

一个优雅的声纹小球动画 Android 组件库，支持 XML 配置和代码动态修改。

## 特性

- 🎨 **丰富的自定义属性**：支持 XML 配置和代码动态修改
- 🎯 **灵活的动画控制**：可配置动画速度、灵敏度、小球数量等
- 📱 **兼容性强**：支持 Android 5.0+ (API 24+)
- 🚀 **易于集成**：一行 Gradle 依赖即可引入
- 💡 **完整示例**：提供 Demo 工程展示各种用法

## 效果展示

声纹小球动画支持两种状态：
- **待机状态**：小球轻微起伏，呈现呼吸效果
- **讲话状态**：根据输入振幅，小球从前往后依次沿曲线跳动
![f53074230cb8d7a331d0feb409c81020](https://github.com/user-attachments/assets/e415992d-4b74-46b0-a66a-f22a9cc17713)


## 快速开始

### 1. 添加依赖

#### JitPack 方式（推荐）

在项目根目录的 `build.gradle` 中添加：

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

在模块的 `build.gradle` 中添加：

```gradle
dependencies {
    implementation 'com.github.voiceprintball:VoiceprintBall:1.0.0'
}
```

#### 本地模块方式

```gradle
dependencies {
    implementation project(':library')
}
```

### 2. XML 布局中使用

```xml
<com.voiceprintball.view.VoiceWaveView
    android:id="@+id/voice_wave"
    android:layout_width="match_parent"
    android:layout_height="120dp"
    app:voicewave_ballColor="#FFFFFF"
    app:voicewave_ballCount="3"
    app:voicewave_animationSpeed="1.0"
    app:voicewave_amplitudeSensitivity="1.25" />
```

### 3. 代码中使用

```kotlin
val voiceWaveView = findViewById<VoiceWaveView>(R.id.voice_wave)

// 设置小球颜色
voiceWaveView.setBallColor(Color.WHITE)

// 输入振幅值（0-1）
voiceWaveView.pushAmplitude01(0.5f)

// 设置动画速度
voiceWaveView.setAnimationSpeed(1.5f)

// 设置声纹灵敏度
voiceWaveView.setAmplitudeSensitivity(1.5f)

// 设置小球数量
voiceWaveView.setBallCount(5)
```

## 属性说明

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `voicewave_ballColor` | color | #FFFFFF | 小球颜色 |
| `voicewave_ballCount` | integer | 3 | 小球数量（1-10） |
| `voicewave_ballRadius` | dimension | 6.8dp | 小球基础半径 |
| `voicewave_ballGap` | dimension | 20dp | 小球间距 |
| `voicewave_maxJumpHeight` | dimension | 14dp | 最大跳跃高度 |
| `voicewave_animationSpeed` | float | 1.0 | 动画速度因子 |
| `voicewave_amplitudeSensitivity` | float | 1.25 | 声纹灵敏度（振幅缩放因子） |
| `voicewave_idleAmplitude` | float | 0.16 | 待机动画幅度（0-1） |
| `voicewave_smoothFactor` | float | 0.85 | 平滑系数（0-1，值越大变化越平滑） |

## API 方法

### 设置方法

- `setBallColor(@ColorInt color: Int)` - 设置小球颜色
- `setBallCount(count: Int)` - 设置小球数量（1-10）
- `setAnimationSpeed(speed: Float)` - 设置动画速度
- `setAmplitudeSensitivity(sensitivity: Float)` - 设置声纹灵敏度
- `pushAmplitude01(value: Float)` - 输入振幅值（0-1）

### 获取方法

- `getBallColor(): Int` - 获取小球颜色
- `getBallCount(): Int` - 获取小球数量
- `getAmplitude01(): Float` - 获取当前振幅值

## 使用场景

### 场景 1：手动控制

```kotlin
seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            voiceWaveView.pushAmplitude01(progress / 100f)
        }
    }
    // ...
})
```

### 场景 2：音频输入

```kotlin
// 在音频采集回调中
audioRecorder.setOnAmplitudeListener { amplitude ->
    // amplitude 范围 0-1
    voiceWaveView.pushAmplitude01(amplitude)
}
```

### 场景 3：模拟数据

```kotlin
val handler = Handler(Looper.getMainLooper())
val runnable = object : Runnable {
    override fun run() {
        val amplitude = Random.nextFloat()
        voiceWaveView.pushAmplitude01(amplitude)
        handler.postDelayed(this, 100) // 每 100ms 更新
    }
}
handler.post(runnable)
```

## Demo 工程

项目包含完整的 Demo 工程（`app` 模块），展示了：
- 基础用法
- XML 属性配置
- 代码动态修改
- 手动控制振幅
- 自动模拟语音输入

运行 Demo：
1. 克隆项目
2. 用 Android Studio 打开
3. 运行 `app` 模块

## 技术实现

- 使用 `ValueAnimator` 实现流畅的 60fps 动画（底层基于 Choreographer，与系统 VSYNC 同步）
- 使用贝塞尔曲线实现小球跳跃轨迹
- 使用平滑算法实现振幅过渡效果
- 支持自定义属性，参考 ShapeView 的设计模式

## 兼容性

- **最低支持版本**：Android 5.0 (API 24)
- **编译版本**：Android 14 (API 36)
- **Kotlin 版本**：2.0.21

## 许可证

本项目采用 MIT 许可证。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 更新日志

### 1.0.0 (2026-01-12)
- ✨ 初始版本发布
- ✨ 支持 XML 属性配置
- ✨ 支持代码动态修改
- ✨ 提供完整的 Demo 工程

