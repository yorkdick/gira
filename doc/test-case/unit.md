# ユニットテスト仕様書

## プロジェクト管理テスト (test_project.py)

| テストケース名                 | 説明                                 | 期待結果                                                                                                                           |
| ------------------------------ | ------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------- |
| test_index                     | プロジェクト一覧ページのテスト       | - ログインユーザーがプロジェクト一覧ページにアクセスできる<br>- ステータスコード 200 が返される                                    |
| test_list_projects             | プロジェクト一覧 API 基本機能テスト  | - ログインユーザーがプロジェクト一覧を取得できる<br>- レスポンスにプロジェクトリストが含まれる<br>- 返されるプロジェクト数が正しい |
| test_list_projects_with_search | プロジェクト一覧検索機能テスト       | - 名前による検索が機能する<br>- ステータスによる検索が機能する<br>- 複合条件による検索が機能する                                   |
| test_create_project            | プロジェクト作成 API テスト          | - 新規プロジェクトが作成できる<br>- プロジェクトデータが DB に正しく保存される<br>- 返されるプロジェクト情報が完全である           |
| test_update_project            | プロジェクト更新 API テスト          | - 既存プロジェクトが更新できる<br>- 更新後のデータが DB に正しく保存される<br>- プロジェクトステータスが変更できる                 |
| test_delete_project            | プロジェクト削除 API テスト          | - プロジェクトが削除できる<br>- プロジェクトステータスが deleted に更新される<br>- DB のプロジェクトステータスが変更される         |
| test_project_detail            | プロジェクト詳細ページテスト         | - プロジェクト詳細ページにアクセスできる<br>- ステータスコード 200 が返される                                                      |
| test_project_to_dict           | プロジェクトモデルシリアライズテスト | - プロジェクトオブジェクトが辞書形式に変換される<br>- 必要なフィールドがすべて含まれる<br>- フィールド値が正確である               |
| test_project_list_api          | プロジェクト一覧 API 完全機能テスト  | - プロジェクトデータが DB に存在する<br>- ユーザーログイン状態が正しい<br>- API レスポンスが完全かつ正確である                     |

## カンバンボード機能テスト (test_kanban.py)

| テストケース名                    | 説明                                           | 期待結果                                                                                                      |
| --------------------------------- | ---------------------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| test_kanban_index_without_project | プロジェクト未選択時のカンバンボード表示テスト | - デフォルトのカンバンボードページが表示される<br>- ステータスコード 200 が返される                           |
| test_kanban_index_with_project    | プロジェクト選択時のカンバンボード表示テスト   | - 特定プロジェクトのカンバンボードページが表示される<br>- ステータスコード 200 が返される                     |
| test_update_story_status          | ストーリーステータス更新テスト                 | - ストーリーステータスが更新できる<br>- ステータス変更が正しく保存される<br>- ステータスコード 200 が返される |
| test_update_story_status_invalid  | 無効なステータス更新テスト                     | - 無効なステータスでのエラー処理が機能する<br>- ステータスコード 400 が返される                               |

## バックログ機能テスト (test_backlog.py)

| テストケース名                     | 説明                                       | 期待結果                                                                                                   |
| ---------------------------------- | ------------------------------------------ | ---------------------------------------------------------------------------------------------------------- |
| test_backlog_index_without_project | プロジェクト未選択時のバックログ表示テスト | - デフォルトのバックログページが表示される<br>- ステータスコード 200 が返される                            |
| test_backlog_index_with_project    | プロジェクト選択時のバックログ表示テスト   | - 特定プロジェクトのバックログページが表示される<br>- ステータスコード 200 が返される                      |
| test_get_stories                   | ストーリー一覧取得テスト                   | - プロジェクトのストーリー一覧が取得できる<br>- ステータスコード 200 が返される                            |
| test_delete_story                  | ストーリー削除テスト                       | - スプリントからストーリーを削除できる<br>- ストーリーの sprint_id が正しくクリアされる                    |
| test_create_sprint                 | スプリント作成テスト                       | - 新規スプリントが作成できる<br>- スプリントデータが正しく保存される<br>- 初期ステータスが planning である |
| test_start_sprint                  | スプリント開始テスト                       | - スプリントが開始できる<br>- スプリントステータスが active に更新される                                   |
| test_finish_sprint                 | スプリント完了テスト                       | - スプリントが完了できる<br>- スプリントステータスが completed に更新される                                |