# Coding Agent Context 扩展

这个内置扩展用于管理当前激活集成所使用的 **编码代理上下文/说明文件**，例如 `CLAUDE.md`、`.github/copilot-instructions.md`、`AGENTS.md`、`GEMINI.md` 等。

它负责维护一个由可配置开始/结束标记包围的受管区块，默认标记是 `<!-- SPECKIT START -->` 和 `<!-- SPECKIT END -->`。

## 为什么要做成扩展？

并不是所有 Spec Kit 用户都希望 Spec Kit 自动写入编码代理的上下文文件。把这部分行为拆成独立扩展后，用户可以：

- 用 `specify extension disable agent-context` **完全关闭** 这项能力，这样 Spec Kit 就不会再创建或修改 agent 上下文文件
- 通过修改 `.specify/extensions/agent-context/agent-context-config.yml` **自定义标记**，Python 层和脚本层都会读取同一份 `context_markers`
- 通过 `/speckit.agent-context.update` **按需刷新**，也可以依赖 `extension.yml` 中声明的 hooks（`after_specify`、`after_plan`）自动刷新

## 命令

| 命令 | 说明 |
|------|------|
| `speckit.agent-context.update` | 用当前 plan 路径刷新 agent 上下文文件中的受管区块 |

## 配置

所有配置都来自这个扩展自己的配置文件：
`.specify/extensions/agent-context/agent-context-config.yml`

```yaml
# 由本扩展管理的编码代理上下文文件路径
context_file: CLAUDE.md

# Spec Kit 受管区块的分隔标记
context_markers:
  start: "<!-- SPECKIT START -->"
  end: "<!-- SPECKIT END -->"
```

- `context_file`：项目内相对路径，指向编码代理上下文文件，由 `specify init` 和 `specify integration install` 写入
- `context_markers.start` / `.end`：受管区块的开始/结束标记，如需自定义请修改这里

## 依赖要求

内置更新脚本需要：
- **Python 3**
- **PyYAML**，用于 YAML 处理和区块更新

`PyYAML` 通常会随着 `specify` CLI 一起可用，并通过相同的 `python3` 解释器运行。  
如果 hook 提示 *"PyYAML is required … not available in the current Python environment"*，通常说明系统里的 `python3` 和安装 Spec Kit 时使用的 Python 不是同一个。可以这样解决：

```bash
pip install pyyaml
# 或者安装到 Spec Kit 使用的那个 Python：
/path/to/speckit-python -m pip install pyyaml
```

## 禁用方式

```bash
specify extension disable agent-context
```

禁用后，Spec Kit 会跳过上下文文件的创建、更新和移除。这些开关逻辑在 `upsert_context_section()` 和 `remove_context_section()` 中处理。
