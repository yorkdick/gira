# GIRA - シンプルなスクラム管理ツール

## 概要

GIRA は、スクラム開発チーム向けの軽量なプロジェクト管理ツールです。シンプルで使いやすい機能に絞って実装しています。

![ロゴ](doc/image/logo.png)

## 主な機能

### 1. プロジェクト管理

- プロジェクト一覧表示
- プロジェクト作成・編集
- チームメンバー管理

### 2. バックログ管理

- プロダクトバックログの作成・編集
- スプリントプランニング
- ドラッグ&ドロップでのストーリー移動
- ストーリーポイントの管理

### 3. かんばんボード

- ToDo/進行中/完了の 3 列表示
- ドラッグ&ドロップでのタスク状態更新
- フィルタリング機能
- スプリント進捗の可視化

### 4. ユーザー管理

- ログイン/ログアウト
- 基本的なユーザー情報管理
- 権限管理

## 技術スタック

### バックエンド

| ライブラリ       | バージョン | 用途                |
| ---------------- | ---------- | ------------------- |
| Python           | 3.8+       | 実行環境            |
| Flask            | 2.3.3      | Web フレームワーク  |
| Flask-SQLAlchemy | 3.0.5      | ORM ツール          |
| Flask-Login      | 0.6.2      | 認証管理            |
| Flask-WTF        | 1.1.1      | フォーム処理        |
| Flask-Migrate    | 4.0.4      | DB マイグレーション |
| SQLite           | 3.x        | データベース        |

### フロントエンド

| ライブラリ  | バージョン | 用途              |
| ----------- | ---------- | ----------------- |
| Bootstrap   | 5.3.0      | UI フレームワーク |
| jQuery      | 3.7.0      | DOM 操作          |
| Sortable.js | 1.15.0     | ドラッグ&ドロップ |
| Chart.js    | 4.3.0      | グラフ描画        |

### 開発ツール

| ツール     | バージョン | 用途                 |
| ---------- | ---------- | -------------------- |
| pytest     | 7.4.0      | ユニットテスト       |
| pytest-cov | 4.1.0      | カバレッジ測定       |
| black      | 23.7.0     | コードフォーマッター |
| flake8     | 6.1.0      | リンター             |

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
│   ├── database.md
│   ├── ui-design.md
│   ├── api-spec.md
│   └── infrastructure.md
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

## 開発環境のセットアップ

### 前提条件

- Python 3.8 以上
- pip
- Git

### インストール手順

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

### 開発サーバーの起動オプション

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

## 詳細設計ドキュメント

- [データベース設計](doc/database.md)
- [画面設計](doc/ui-design.md)
- [API 設計](doc/api-spec.md)
- [インフラ構成](doc/infrastructure.md)
- [画面詳細設計](doc/screen-design-detail.md)
- [テストケース](doc/test-cases.md)

## 動作確認済み環境

- OS: Windows 10/11, macOS, Linux
- ブラウザ: Chrome, Firefox, Safari（最新版）
- Python: 3.8 以上

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

## ライセンス

MIT License
