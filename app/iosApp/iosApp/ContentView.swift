import SwiftUI
import Shared

struct ContentView: View {
    @State private var summaryText = "点击按钮后，会通过 Swift export 调用 Kotlin。"
    @State private var asyncMessage = "还没有执行异步调用。"

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text("Swift export 演示")
                    .font(.largeTitle.bold())

                Text("这个页面演示的是：不再走旧的 Objective-C 风格桥接，而是通过 Swift export 直接调用 Kotlin API。")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)

                VStack(alignment: .leading, spacing: 12) {
                    Text("1. 普通 class / enum / object")
                        .font(.headline)

                    Button("生成 Kotlin 摘要") {
                        let summary = SwiftExportSamples.shared.makeSummary(name: "小明")
                        summaryText = "\(summary.headline) [\(summary.state.label)]"
                    }
                    .buttonStyle(.borderedProminent)

                    Text(summaryText)
                        .textSelection(.enabled)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("2. suspend 函数 -> async")
                        .font(.headline)

                    Button("执行异步 Kotlin 调用") {
                        Task {
                            do {
                                asyncMessage = try await SwiftExportSamples.shared.loadWelcomeMessage(name: "手机用户")
                            } catch {
                                asyncMessage = "异步调用失败：\(error.localizedDescription)"
                            }
                        }
                    }
                    .buttonStyle(.bordered)

                    Text(asyncMessage)
                        .textSelection(.enabled)
                }

                Text("Flow 到 AsyncSequence 的导出也会生成，但因为 Swift export 目前还是 Alpha，我先移除了这段实时 UI 演示，避免 Xcode 在不同缓存状态下出现不稳定编译。")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(Color(uiColor: .systemGroupedBackground))
    }
}
