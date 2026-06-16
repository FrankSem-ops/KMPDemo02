import SwiftUI
import Shared

struct ContentView: View {
    @State private var showGreeting = false

    var body: some View {
        VStack(spacing: 24) {
            Button(showGreeting ? "Hide greeting" : "Show greeting") {
                showGreeting.toggle()
            }
            .buttonStyle(.borderedProminent)

            if showGreeting {
                Text("Kotlin says: \(Greeting().greet())")
                    .font(.headline)
                    .multilineTextAlignment(.center)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(24)
        .background(Color(uiColor: .systemGroupedBackground))
    }
}
