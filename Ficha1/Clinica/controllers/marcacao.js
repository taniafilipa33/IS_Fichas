const { query } = require("express");
var mysql = require("mysql");

let connection = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "password",
  database: "clinica",
});

module.exports.getPedidos = function () {
  connection.connect(function (err) {
    if (err) throw err;
    return connection.query("SELECT * FROM Marcacao");
  });
};

module.exports.getPedido = function (id) {
  connection.connect(function (err) {
    if (err) throw err;
    connection.query(
      "SELECT * FROM Marcacao where idMarcacao=" + id,
      function (err, result, fields) {
        if (err) throw err;
        console.log(result);
        return result;
      }
    );
  });
};
