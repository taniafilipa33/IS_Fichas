var mysql = require("mysql");

module.exports.getPedidos = async () => {
  return await executaQuery("SELECT * FROM Pedido");
};

module.exports.getLast = async () => {
  var idJog = await executaQuery("(select max(idPedido) from Pedido)");
  return JSON.parse(JSON.stringify(idJog))[0]["max(idPedido)"];
};

module.exports.insertPedido = async (pedido, idConsulta) => {
  return await executaQuery(
    "insert into Pedido(mensagem, estado, data, idConsulta,relatorio, codigoExame, descExame) values('" +
      pedido.mensagem +
      "','" +
      pedido.estado +
      "','" +
      pedido.data +
      "'," +
      idConsulta +
      "," +
      pedido.relatorio +
      ",'" +
      pedido.codigoExame +
      "','" +
      pedido.descExame +
      "')"
  );
};

const executaQuery = (query) => {
  return new Promise((resove, reject) => {
    let connection = mysql.createConnection({
      host: "localhost",
      user: "root",
      password: "password",
      database: "hospital",
    });

    connection.connect(function (err) {
      if (err) return console.error("error: " + err.message);
      else {
        connection.query(query, (erro, result) => {
          if (erro) reject(erro);
          else {
            console.log(result);
            resove(result);
          }
        });
      }
    });
  }).catch((erro) => console.log(erro));
};
