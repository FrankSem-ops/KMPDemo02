import SwiftUI
import Shared

struct ContentView: View {
    var body: some View {
        SharedComposeView()
            .ignoresSafeArea()
    }
}

private struct SharedComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        SharedUiHost.shared.makeViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
