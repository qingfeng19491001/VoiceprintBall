# VoiceWaveView

一个优雅的声纹小球动画 Android 组件库，专为语音识别、语音助手等需要展示音频输入状态的场景设计，提供了流畅的视觉反馈效果。支持 XML 配置和代码动态修改。

## 特性

- 🎨 **丰富的自定义属性**：支持 XML 配置和代码动态修改，参考 ShapeView 的设计模式
- 🎯 **灵活的动画控制**：可配置动画速度、灵敏度、小球数量等
- 📱 **兼容性强**：支持 Android 5.0+ (API 24+)，兼容不同屏幕尺寸
- 🚀 **易于集成**：一行 Gradle 依赖即可引入，支持 JitPack 发布
- 💡 **完整示例**：提供 Demo 工程展示各种用法，包含详细的 KDoc 文档
- ⚡ **高性能**：基于 Canvas 的自定义 View，使用 Choreographer 实现流畅的动画（目标 60fps）

## 效果展示

声纹小球动画支持两种状态：
- **待机状态**：小球轻微起伏，呈现呼吸效果（使用正弦波实现）
- **讲话状态**：根据输入振幅，小球从前往后依次沿贝塞尔曲线跳动，通过相位延迟实现从左到右的波浪传播动画

## 应用场景

该组件可用于以下场景：
- 🎤 **语音识别应用**：实时展示语音输入状态和音量变化
- 🤖 **语音助手**：提供直观的音频输入视觉反馈
- 📞 **语音通话**：显示通话中的音频活动状态
- 🎵 **音频录制**：可视化音频录制过程中的音量变化

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
    implementation 'com.github.qingfeng19491001:VoiceprintBall:1.0.0'
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

### 核心实现

1. **基于 Canvas 的自定义 View**
   - 实现了 `VoiceWaveView` 继承自 `View`，重写 `onDraw()` 方法进行自定义绘制
   - 使用 `Paint` 进行抗锯齿绘制，确保小球边缘平滑

2. **Choreographer 实现流畅动画**
   - 在 `onAttachedToWindow()` 中注册 `Choreographer.FrameCallback`
   - 每帧更新动画时间 `t` 和平滑振幅 `smoothAmp`
   - 在 `onDetachedFromWindow()` 中正确移除回调，避免内存泄漏
   - 使用系统帧同步机制，确保动画性能稳定（目标 60fps，实际帧率取决于设备性能）

3. **贝塞尔曲线算法实现跳跃轨迹**
   - 使用二次贝塞尔曲线 `bezierArcY(t)` 计算小球垂直位置
   - 控制点设置为 `p0=0, p1=1, p2=0`，在 `t=0.5` 时达到最大值 0.5，形成平滑的跳跃弧线
   - 公式：`y = (1-t)²·0 + 2(1-t)·t·1 + t²·0 = 2(1-t)·t`
   - 返回值范围 0-0.5，通过乘以 `maxJump` 得到实际跳跃高度

4. **相位延迟实现波浪传播**
   - 通过 `offset = startOffset + i * 0.9f` 为每个小球设置不同的相位偏移
   - 使用 `wave01(t + offset)` 计算每个小球的波形值
   - 实现从左到右的波浪传播效果，视觉上自然流畅

5. **平滑插值算法避免动画突变**
   - 使用指数平滑算法：`smoothAmp = smoothAmp * smoothFactor + amp01 * (1 - smoothFactor)`
   - 默认 `smoothFactor = 0.85`，可根据需要调整
   - 实现待机状态（轻微起伏）和语音输入状态（大幅跳动）的无缝切换

6. **动态振幅输入支持**
   - `pushAmplitude01(value: Float)` 方法接收 0-1 范围的振幅值
   - 0 表示待机状态，1 表示最大振幅
   - 支持实时更新，适用于音频采集回调

7. **完整的示例 Activity**
   - `WaveformActivity` 提供了两种演示模式：
     - **手动控制**：通过 `SeekBar` 实时控制振幅值
     - **自动模拟**：使用 `Handler` 和 `Runnable` 每 100ms 生成随机振幅，模拟语音输入
   - 包含完整的生命周期管理，避免内存泄漏

8. **详细的 KDoc 文档**
   - 所有公共方法都有详细的 KDoc 注释
   - 包含使用示例和参数说明
   - 便于开发者快速理解和使用

### 设计模式

- 参考 ShapeView 的 `app:shape_xxx` 属性设计模式
- 支持 XML 属性配置和代码动态修改
- 提供完整的 getter/setter 方法

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
- ✨ 实现基于 Canvas 的自定义 View `VoiceWaveView`
- ✨ 使用 Choreographer 实现流畅的动画（目标 60fps）
- ✨ 使用贝塞尔曲线算法实现多个小球的波浪式跳动效果（默认 3 个，支持 1-10 个）
- ✨ 通过相位延迟实现从左到右的波浪传播动画
- ✨ 支持动态振幅输入（0-1 范围），实现待机状态和语音输入状态的无缝切换
- ✨ 使用平滑插值算法避免动画突变
- ✨ 支持 XML 属性配置和代码动态修改
- ✨ 提供完整的示例 Activity `WaveformActivity`，包含手动控制和自动模拟两种演示模式
- ✨ 添加详细的 KDoc 文档和使用示例
- ✨ 支持 JitPack 发布，一行 Gradle 依赖即可引入

