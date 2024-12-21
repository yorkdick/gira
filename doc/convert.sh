#!/bin/bash

# 获取脚本所在目录的绝对路径
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# 切换到项目根目录
cd "$PROJECT_ROOT"

# 首先生成参考文档（如果不存在）
if [ ! -f "doc/reference.docx" ]; then
    pandoc --print-default-data-file reference.docx > doc/reference.docx
    echo "已生成参考文档 doc/reference.docx，请使用 Word 编辑样式后再次运行此脚本"
    exit 0
fi

# 转换命令
pandoc "doc/screen-design-detail.md" \
  --from markdown \
  --reference-doc="doc/reference.docx" \
  --toc \
  --toc-depth=3 \
  --number-sections \
  -V lang=ja-JP \
  --wrap=none \
  --resource-path=".:doc:doc/image" \
  -o "画面詳細設計書.docx"

echo "文档已生成：$PROJECT_ROOT/画面詳細設計書.docx"