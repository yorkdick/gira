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
│   ├── __init__.py        # アプリケーション初期化
│   ├── models/            # データモデル
│   │   ├── __init__.py
│   │   ├── user.py       # ユーザーモデル
│   │   ├── project.py    # プロジェクトモデル
│   │   ├── story.py      # ストーリーモデル
│   │   └── sprint.py     # スプリントモデル
│   ├── views/            # ビュー
│   │   ├── __init__.py
│   │   ├── auth.py      # 認証関連
│   │   ├── project.py   # プロジェクト管理
│   │   ├── backlog.py   # バックログ管理
│   │   ├── kanban.py    # カンバンボード
│   │   └── main.py      # メインページ
│   ├── static/           # 静的ファイル
│   │   ├── css/
│   │   ├── js/
│   │   └── img/
│   └── templates/        # テンプレート
│       ├── base.html
│       ├── auth/
│       ├── project/
│       └── board/
├── instance/             # インスタンス固有のファイル
│   ├── gira.db          # 本番用データベース
│   └── giratest.db      # テスト用データベース
├── tests/               # テストコード
│   ├── __init__.py
│   ├── test_project.py  # プロジェクト機能のテスト
│   ├── test_backlog.py  # バックログ機能のテスト
│   └── test_kanban.py   # カンバンボード機能のテスト
├── doc/                 # ドキュメント
│   ├── basic-design/    # 基本設計書
│   │   ├── database.md      # データベース設計
│   │   ├── ui-design.md     # UI設計
│   │   ├── api-spec.md      # API仕様
│   │   └── infrastructure.md # インフラ設計
│   ├── detail-design/   # 詳細設計書
│   │   ├── database.md      # データベース詳細設計
│   │   ├── ui-design.md     # UI詳細設計
│   │   ├── api-spec.md      # API詳細仕様
│   │   └── infrastructure.md # インフラ詳細設計
│   ├── manual/         # マニュアル
│   │   ├── startup.md       # 開発環境セットアップ
│   │   └── operation.md     # 運用マニュアル
│   └── test-case/      # テストケース
│       ├── unit-test/       # 単体テスト仕様
│       └── integration-test/ # 結合テスト仕様
├── migrations/          # DBマイグレーション
│   ├── versions/       # マイグレーションファイル
│   ├── env.py         # マイグレーション環境設定
│   ├── README         # マイグレーション説明
│   └── alembic.ini    # Alembic設定
├── scripts/            # 各種スクリプト
│   └── init_db.py     # DB初期化スクリプト
├── logs/              # ログファイル
├── htmlcov/           # カバレッジレポート
├── .env              # 環境変数
├── config.py         # 設定ファイル
├── pytest.ini        # pytestの設定
├── requirements.txt  # 依存パッケージ
└── wsgi.py          # WSGIエントリーポイント
```

## 自動化テスト

### テストの実行

```bash
# 全テストの実行
pytest

# 特定のテストファイルの実行
pytest tests/test_project.py

# 特定のテスト関数の実行
pytest tests/test_project.py::test_create_project

# 詳細なテスト結果を表示
pytest -v

# テストカバレッジレポートの生成
pytest --cov=app --cov-report=html
```

### テストデータベース

テストでは本番データベースとは別の専用データベースを使用します：

- 本番用 DB: `instance/gira.db`
- テスト用 DB: `instance/giratest.db`

### テストの設定

`pytest.ini` にテストの基本設定が記述されています：

```ini
[pytest]
addopts = --strict-markers --cov=app --cov-report=term-missing --cov-report=html -v --tb=short
filterwarnings =
    ignore::DeprecationWarning
    ignore::PendingDeprecationWarning
```

この設定により：

- コードカバレッジレポートが自動生成されます（HTML 形式）
- 未テストのコード行が表示されます
- 詳細なテスト結果が表示されます

### テストレポート

テスト実行後、以下のレポートが生成されます：

- ターミナル出力：テスト結果の概要と未テスト行の情報
- HTML レポート：`htmlcov/index.html` に詳細なカバレッジ情報
  - ファイルごとのカバレッジ率
  - 未テスト行のハイライト表示
  - カバレッジの詳細な統計情報
