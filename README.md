# GIRA - シンプルなスクラム管理ツール

## 概要

GIRA は、スクラム開発チーム向けの軽量なプロジェクト管理ツールです。シンプルで使いやすい機能に絞って実装しています。

![ロゴ](doc/assets/image/logo.png)

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

## マニュアル

- [開発環境のセットアップ](doc/manual/startup.md)

## 設計ドキュメント

### 基本設計書

- [データベース設計](doc/basic-design/database.md)
- [画面設計](doc/basic-design/ui-design.md)
- [API 設計](doc/basic-design/api-spec.md)
- [インフラ構成](doc/basic-design/infrastructure.md)

### 詳細設計書

- [データベース設計](doc/detail-design/database.md)
- [画面設計](doc/detail-design/ui-design.md)
- [API 設計](doc/detail-design/api-spec.md)
- [インフラ構成](doc/detail-design/infrastructure.md)

### テスト設計書

- [テストケース](doc/test-case/test-cases.md)

## 動作確認済み環境

- OS: Windows 10/11, macOS, Linux
- ブラウザ: Chrome, Firefox, Safari（最新版）
- Python: 3.8 以上

## ライセンス

MIT License
