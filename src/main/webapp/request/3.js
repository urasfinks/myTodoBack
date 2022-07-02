function main(state, personKey){
  var obj = {
    sql: "select * from \"data\" where id_data = ${id}",
    args: [
      {
        field: 'data',
        type: 'VARCHAR',
        direction: 'COLUMN'
      },
      {
        field: 'id',
        type: 'NUMBER',
        direction: 'IN',
        value: '1'
      }
    ]
  };
  return Java.type('ru.jamsys.JS').sql(JSON.stringify(obj));
}