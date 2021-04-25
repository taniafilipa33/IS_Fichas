var mysql = require("mysql");
const axios = require("axios");
var Paciente = require("../controllers/Paciente");

module.exports.updateJSONHospital = (pedidos) => {
  axios
    .get("http://localhost:3000/pedidos")
    .then((dados) => {
      pedidos.forEach((element) => {
        dados.data.forEach((pped) => {
          if (element.id == pped.id) {
            console.log(pped);
            if (element.relatorio) {
              //se tiver relatorio atualiza

              axios
                .put("http://localhost:3000/pedidos/" + element.id, {
                  id: element.id,
                  estado: element.estado,
                  data: element.data,
                  relatorio: element.relatorio,
                  codigoExame: element.codigoExame,
                  descExame: element.descExame,
                })
                .then((e) => {})
                .catch((e) => {
                  console.log(e);
                });
            }
          }
        });
      });
    })
    .catch((e) => {
      console.log(e);
    });
};

module.exports.getPedidos = async () => {
  return await executaQuery("SELECT * FROM Pedido");
};

module.exports.getLast = async () => {
  var idJog = await executaQuery("(select max(idPedido) from Pedido)");
  return JSON.parse(JSON.stringify(idJog))[0]["max(idPedido)"];
};

module.exports.updatePeds = async (pedidos) => {
  var p = pedidos.map(async (ped) => {
    if (!ped.relatorio) var rel = null;
    else var rel = ped.relatorio;
    await executaQuery(
      "Update Pedido set mensagem=null, estado='" +
        ped.estado +
        "', relatorio='" +
        rel +
        "' where idPedido=" +
        ped.id
    );
  });
  await Promise.all(p);
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
