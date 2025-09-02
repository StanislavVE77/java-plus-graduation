# Comments Feature

## Private: User actions with comments

**Base URL**: `/users/{userId}/events/{eventId}/comments`

### Create Comment
```http
POST /users/{userId}/events/{eventId}/comments
Content-Type: application/json
```
**Body**:
```json
{
  "text": "Your comment here"
}
```
**Response**: `201 Created`  
Returns `CommentDtoResponse`.

---

### Update Comment
```http
PATCH /users/{userId}/events/{eventId}/comments/{commId}
Content-Type: application/json
```
**Body**:
```json
{
  "text": "Updated comment text"
}
```
**Response**: `200 OK`  
Returns updated `CommentDtoResponse`.

---

### Delete Comment
```http
DELETE /users/{userId}/events/{eventId}/comments/{commId}
```
**Response**: `204 No Content`

---

### Get User Comments for Event
```http
GET /users/{userId}/events/{eventId}/comments
```
**Response**: `200 OK`  
Returns list of `CommentDtoResponse`.

---

## Admin: Manage all comments

**Base URL**: `/admin/comments`

### Get Comments with Filters
```http
GET /admin/comments?users=1,2&events=3,4&rangeStart=2024-01-01 00:00:00&rangeEnd=2024-12-31 23:59:59&from=0&size=10
```
**Response**: `200 OK`  
Returns list of `CommentDtoResponse`.

---

### Delete Any Comment
```http
DELETE /admin/comments/{commentId}
```
**Response**: `204 No Content`

---

## Public: View comments

**Base URL**: `/comments`

### Get Comment by ID
```http
GET /comments/{commentId}
```
**Response**: `200 OK`  
Returns `CommentDtoResponse`.

---

### Get Comments by Event
```http
GET /comments/events/{eventId}
```
**Response**: `200 OK`  
Returns list of `CommentDtoResponse`.

---

> All date/time parameters follow format: `yyyy-MM-dd HH:mm:ss`
>
> Authentication and authorization are assumed to be handled globally.
