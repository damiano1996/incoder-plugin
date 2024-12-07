# InCoder: An LLM-Powered Plugin for JetBrains IDEs

<!-- Plugin description -->
**InCoder** is a powerful plugin designed for JetBrains IDEs, including IntelliJ IDEA, PyCharm, and others in the JetBrains ecosystem. 
It seamlessly integrates advanced Large Language Models (LLMs) into your development workflow, providing you with code generation, understanding, and completion capabilities, all directly within your favorite IDE.
<!-- Plugin description end -->

<p align="center">
  <img src=".github/readme/demo-chat-hello-world-01.png" alt="Demo Chat Hello World 01" style="width: 45%; margin-right: 10px;">
  <img src=".github/readme/demo-chat-hello-world-02.png" alt="Demo Chat Hello World 02" style="width: 45%;">
</p>


---

## Key Features

### 1. **Interactive Chat for Code Assistance**

<p align="center">
  <img src=".github/readme/demo-chat.png" alt="Demo Chat" style="width: 45%; margin-right: 10px;">
  <img src=".github/readme/demo-chat-merge.png" alt="Demo Chat Merge" style="width: 45%;">
</p>


- A dedicated **tool window** appears on the right panel of the IDE after installing the plugin.
- Interact with an LLM to:
  - **Generate code snippets** or solve coding challenges.
  - **Understand code** by analyzing the file or specific lines you're viewing.
  - Get contextual suggestions and explanations based on the active file you're working on, as the LLM has access to the current code.

### 2. **Inline Code Completion**

![Demo Inline](.github/readme/demo-inline.png)

- **Real-time suggestions** while you type in the editor.
- Press **Tab** to accept the suggested code and insert it directly into your file.
- Accelerate your coding workflow with intelligent autocompletion that understands the context of your project.

### 3. **Support for Multiple LLM Providers**
- InCoder supports **multiple LLM providers**, giving you flexibility and choice:
  - **Ollama**: Utilize models running locally on your machine to preserve privacy.
  - **OpenAI**: Access advanced cloud-based LLMs for high-quality suggestions and assistance.
  - **Anthropic**: Integrate with this leading LLM provider for ethical and powerful AI capabilities.
- All providers can be easily **configured** through the plugin's settings, allowing you to choose the one that best fits your needs.

### 4. **Privacy-Focused Local LLM Support**
- By using **Ollama**, InCoder enables local LLM inference directly on your computer.
- Keeps sensitive project data private and ensures compliance with internal security policies.
- Ideal for developers who value privacy and want to avoid sending data to external servers.

## Settings
The plugin is highly customizable through the **InCoder Settings** panel in your IDE. Access it via the settings/preferences menu under the section **InCoder**. 
Configuration is divided into three main subsections:

- **Chat**: Configure settings for the interactive chat window, such as history retention and UI preferences.
- **Inline**: Enable or disable inline code completion and customize the behavior (e.g., auto-suggestions, Tab behavior).
- **Server**: Set up your preferred LLM provider.

---

## Benefits of Using InCoder
- **Boost productivity**: Generate boilerplate code, debug faster, and understand unfamiliar codebases more quickly.
- **Enhanced contextual understanding**: The LLM has access to your active file, ensuring accurate and relevant suggestions tailored to your project.
- **Customizable and flexible**: Easily switch between LLM providers to adapt to your workflow or organizational requirements.
- **Seamless integration**: Designed specifically for JetBrains IDEs, ensuring an intuitive and native user experience.

## Experimental Features
- **Inline code completion** is currently experimental but highly promising for real-time code assistance and efficiency. Feedback is welcome to improve this feature further.

## Supported JetBrains IDEs
While InCoder is primarily tested on **IntelliJ IDEA** and **PyCharm**, it is compatible with most JetBrains IDEs, making it a versatile choice for developers working in diverse environments.

---

## Contribution
Contributions are welcome! If you'd like to help improve InCoder, follow these steps:
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Commit your changes and submit a pull request.
4. Open **issues** for suggestions, bug reports, or enhancements.

We follow a standard GitHub workflow, so feel free to contribute as you'd like!

---

## License
This project is licensed under the **MIT License**.  
You are free to use, modify, and distribute the code. See the [LICENSE](LICENSE) file for more details.

---

Empower your coding experience with **InCoder** and unlock the full potential of AI-driven development assistance. 
Whether you're generating code, understanding complex algorithms, or seeking intelligent completions, InCoder is here to make your workflow smarter, faster, and more secure.