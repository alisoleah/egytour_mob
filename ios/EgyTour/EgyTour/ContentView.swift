import SwiftUI

struct ContentView: View {
    var body: some View {
        // This is a placeholder or preview wrapper.
        // In the real app, EgytourApp.swift is the entry point.
        LoginView(onLoginSuccess: {
            print("Login success (preview)")
        })
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
