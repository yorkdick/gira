# GIRA - アジャイル開発プロジェクト管理ツール

## 概要

GIRA はシンプルで使いやすいアジャイル開発プロジェクト管理ツールです。

## 機能

- プロジェクト管理
- バックログ管理
- かんばんボード
- スプリント管理
- ユーザー管理

## セットアップ

1. 依存パッケージのインストール

```bash
pip install -r requirements.txt
```

2. データベースの初期化

```bash
python init_db.py
```

3. アプリケーションの起動

```bash
python app.py
```

## テスト実行

### 1. 単体テスト

```bash
# すべての単体テストを実行
python -m pytest tests/unit

# 特定のテストファイルを実行
python -m pytest tests/unit/test_story.py
```

### 2. UI 自動化テスト

#### 前提条件

- Chrome WebDriver のセットアップ（詳細は `doc/manual/startup.md` を参照）
- アプリケーションサーバーが起動していること（http://127.0.0.1:5000）
- テストデータが準備されていること

#### テスト実行コマンド

```bash
# すべてのUIテストを実行
python tests/ui_test.py

# スクリーンショット付きで実行
python tests/ui_test.py --screenshot

# 特定のテストケースのみ実行
python tests/ui_test.py -k "test_login"    # ログインテストのみ
python tests/ui_test.py -k "test_kanban"   # かんばんテストのみ
python tests/ui_test.py -k "test_backlog"  # バックログテストのみ
```

#### テスト結果の確認

- テスト結果：標準出力に表示
- スクリーンショット：`tests/result/*.png`
- ログファイル：`logs/app.log`

## ドキュメント

### 設計書

- 基本設計書: `doc/basic-design/`
- 詳細設計書: `doc/detail-design/`

### テスト関連

- UI テスト仕様書: `doc/test-case/ui-autotest.md`
- 単体テスト仕様書: `doc/test-case/unit-test.md`

### マニュアル

- セットアップ手順: `doc/manual/startup.md`
- 運用マニュアル: `doc/manual/operation.md`

## 動作確認済み環境

- OS: Windows 10/11, macOS 12 以上, Ubuntu 20.04/22.04
- Python: 3.8 以上（3.11 推奨）
- ブラウザ: Google Chrome 最新版

## ライセンス

MIT License
