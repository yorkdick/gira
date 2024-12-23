# 開発環境のセットアップ

## 前提条件

- Python 3.8 以上
- pip
- Git

## インストール手順

```bash
# リポジトリのクローン
git clone https://github.com/your-org/gira.git
cd gira

# 仮想環境の作成と有効化
python -m venv venv
source venv/bin/activate  # Linuxの場合
.\venv\Scripts\activate   # Windowsの場合

# 依存パッケージのインストール
pip install -r requirements.txt

# 環境変数の設定
cp .env.example .env
# .envファイルを編集して必要な設定を行う

# データベースの初期化
flask db upgrade

# 開発サーバーの起動
flask run
```

## 開発サーバーの起動オプション

```bash
# 通常起動
flask run

# ホットリロード有効で起動（開発時推奨）
flask run --debug

# ホストとポートを指定して起動
flask run --host=0.0.0.0 --port=5000

# 環境変数で設定
export FLASK_DEBUG=1  # Unix系の場合
set FLASK_DEBUG=1     # Windowsの場合
flask run
```

## コードフォーマット

### Black の使用方法

```bash
# 単一ファイルのフォーマット
black path/to/file.py

# ディレクトリ内の全Pythonファイルをフォーマット
black .

# 変更箇所のプレビュー（実際には変更を適用しない）
black --diff path/to/file.py

# 行の最大長を指定してフォーマット（デフォルトは88文字）
black --line-length 79 path/to/file.py

# 特定のファイルやディレクトリを除外
black . --exclude "(\.git|\.mypy_cache|\.venv|venv|\.env)"
```

Black は厳格な Python コードフォーマッターで、コードの一貫性を保つために使用します。設定不要で、常に一貫したフォーマットを適用します。

## プロジェクト構成

```
gira/
├── app/                    # アプリケーションコード
│   ├── __init__.py
│   ├── models/            # データモデル
│   │   ├── __init__.py
│   │   ├── user.py
│   │   ├── project.py
│   │   └── story.py
│   ├── views/            # ビュー
│   │   ├── __init__.py
│   │   ├── auth.py
│   │   ├── project.py
│   │   └── board.py
│   ├── static/           # 静的ファイル
│   │   ├── css/
│   │   ├── js/
│   │   └── img/
│   └── templates/        # テンプレート
│       ├── base.html
│       ├── auth/
│       ├── project/
│       └── board/
├── doc/                  # ドキュメント
│   ├── basic-design/
│   │   ├── database.md
│   │   ├── ui-design.md
│   │   ├── api-spec.md
│   │   └── infrastructure.md
│   ├── detail-design/
│   │   ├── database.md
│   │   ├── ui-design.md
│   │   ├── api-spec.md
│   │   └── infrastructure.md
│   ├── test-case/
│   │   └── test-cases.md
├── tests/               # テストコード
│   ├── __init__.py
│   ├── test_auth.py
│   └── test_project.py
├── migrations/         # DBマイグレーション
├── .env               # 環境変数
├── config.py          # 設定ファイル
├── requirements.txt   # 依存パッケージ
└── run.py            # 起動スクリプト
```
