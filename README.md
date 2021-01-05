
mysql --host 127.0.0.1 --port 3306 -u root -p



curl localhost:8080 


curl -X GET -H "Content-Type: application/json" localhost:8080/api/list | jq


curl -X POST -H "Content-Type: application/json" -d '{"nickname":"Kabigon", "message":"ZZZZZZZZZZZ"}' localhost:8080/api/add
