curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy milk","description":"Get milk from store","completed":false}'