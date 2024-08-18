# InCoder Plugin for JetBrains IDEs

<!-- Plugin description -->
**InCoder** is a plugin for JetBrains IDEs that provides advanced, AI-powered code completion using Hugging Face models. The plugin runs entirely on your local machine, ensuring privacy and efficiency without the need for external servers.
<!-- Plugin description end -->

## Features

- **Local AI-Powered Code Completion:** Leveraging Hugging Face models, all inference is performed locally in a Docker container.
- **Multi-IDE Support:** Compatible with IntelliJ IDEA, PyCharm, WebStorm, and other JetBrains IDEs.
- **Open Source:** Fully transparent and customizable to meet your specific needs.
- **Language Agnostic:** Works with various programming languages supported by JetBrains IDEs.
- **Privacy-Focused:** Your code never leaves your machine, ensuring complete data privacy.

## Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/damiano1996/incoder.git
   ```

2. **Build the Plugin:**
    - Open the project in IntelliJ IDEA or your preferred JetBrains IDE.
    - Build the project using `Build > Build Project`.

3. **Install the Plugin:**
    - Go to `Settings/Preferences > Plugins > ⚙️ > Install Plugin from Disk...`
    - Select the built plugin file (usually with a `.jar` extension).
    - Restart your IDE to activate the plugin.

4. **Docker Setup:**
    - Ensure Docker is installed and running on your system.
    - The plugin will automatically manage the Docker container for local inference.

## Usage

1. After installation, configure the plugin via `Settings/Preferences > Tools > InCoder Plugin`.
2. Start coding in your preferred JetBrains IDE.
3. Experience enhanced, AI-powered code completion as you type!

## Configuration

Customize InCoder to suit your workflow:

- **Model Selection:** Choose from a variety of Hugging Face models optimized for code completion.
- **Performance Tuning:** Adjust memory and CPU allocation for the Docker container to balance performance and resource usage.
- **Activation Settings:** Configure when and how the AI-powered completion activates.
- **Language-Specific Settings:** Fine-tune the plugin's behavior for different programming languages.

## Troubleshooting

- **Docker Issues:** Ensure Docker is running and you have sufficient permissions.
- **Performance Concerns:** Adjust the resource allocation in the plugin settings.
- **Compatibility Problems:** Check our [compatibility list](./COMPATIBILITY.md) for IDE version support.

## Contributing

We welcome contributions from the community! Here's how you can help:

1. Fork the repository and create your feature branch.
2. Write clear, commented code and include unit tests where applicable.
3. Ensure your code adheres to our style guidelines.
4. Submit a pull request with a comprehensive description of your changes.

For bug reports or feature requests, please open an issue in our GitHub repository.

## License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for full details.

## Acknowledgments

- Thanks to the Hugging Face team for their incredible models.
- Gratitude to the JetBrains team for their excellent IDE ecosystem.
- Appreciation to all contributors who help make InCoder better.