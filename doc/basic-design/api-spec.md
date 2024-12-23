# API 設計書

## 1. 認証 API

### ログイン

```
POST /api/v1/auth/login
```

#### リクエスト

```json
{
  "username": "string",
  "password": "string"
}
```

#### レスポンス

```json
{
  "token": "string",
  "user": {
    "id": "integer",
    "username": "string",
    "email": "string",
    "role": "string"
  }
}
```

### ログアウト

```
POST /api/v1/auth/logout
```

## 2. プロジェクト API

### プロジェクト一覧取得

```
GET /api/v1/projects
```

#### レスポンス

```json
{
  "projects": [
    {
      "id": "integer",
      "name": "string",
      "key": "string",
      "description": "string"
    }
  ]
}
```

### プロジェクト作成

```
POST /api/v1/projects
```

#### リクエスト

```json
{
  "name": "string",
  "key": "string",
  "description": "string"
}
```

## 3. スプリント API

### スプリント一覧取得

```
GET /api/v1/projects/{project_id}/sprints
```

#### レスポンス

```json
{
  "sprints": [
    {
      "id": "integer",
      "name": "string",
      "goal": "string",
      "start_date": "date",
      "end_date": "date",
      "status": "string"
    }
  ]
}
```

### スプリント作成

```
POST /api/v1/projects/{project_id}/sprints
```

#### リクエスト

```json
{
  "name": "string",
  "goal": "string",
  "start_date": "date",
  "end_date": "date"
}
```

## 4. ストーリー API

### ストーリー一覧取得

```
GET /api/v1/projects/{project_id}/stories
```

#### クエリパラメータ

- sprint_id (optional)
- status (optional)
- assignee_id (optional)

#### レスポンス

```json
{
  "stories": [
    {
      "id": "integer",
      "title": "string",
      "description": "string",
      "status": "string",
      "points": "integer",
      "priority": "string",
      "assignee": {
        "id": "integer",
        "username": "string"
      }
    }
  ]
}
```

### ストーリー作成

```
POST /api/v1/projects/{project_id}/stories
```

#### リクエスト

```json
{
  "title": "string",
  "description": "string",
  "points": "integer",
  "priority": "string",
  "assignee_id": "integer"
}
```

### ストーリー更新

```
PUT /api/v1/stories/{story_id}
```

#### リクエスト

```json
{
  "title": "string",
  "description": "string",
  "status": "string",
  "points": "integer",
  "priority": "string",
  "assignee_id": "integer"
}
```

## 5. エラーレスポンス

### 400 Bad Request

```json
{
  "error": {
    "code": "string",
    "message": "string",
    "details": {}
  }
}
```

### 401 Unauthorized

```json
{
  "error": {
    "code": "unauthorized",
    "message": "認証が必要です"
  }
}
```

### 403 Forbidden

```json
{
  "error": {
    "code": "forbidden",
    "message": "権限がありません"
  }
}
```

### 404 Not Found

```json
{
  "error": {
    "code": "not_found",
    "message": "リソースが見つかりません"
  }
}
```

## 6. 共通仕様

### リクエストヘッダー

```
Authorization: Bearer {token}
Content-Type: application/json
Accept: application/json
```

### ページネーション

```
GET /api/v1/resources?page=1&per_page=20
```

#### レスポンスヘッダー

```
X-Total-Count: 100
X-Page: 1
X-Per-Page: 20
```
