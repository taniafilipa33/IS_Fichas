var mysql = require("mysql");
var axios = require("axios");

module.exports.updateJsonClinica = (pedidos) => {
  var contem;
  axios
    .get("http://localhost:3002/pedidos")
    .then((dados) => {
      pedidos.forEach((element) => {
        contem = 0;
        dados.data.forEach((pped) => {
          if (element.id == pped.id) {
            contem = 1;
            if (!pped.relatorio) {
              //se não contiver relatorio atualiza
              axios
                .put("http://localhost:3002/pedidos/" + element.id, {
                  id: element.id,
                  estado: element.estado,
                  data: element.data,
                  codigoExame: element.codigoExame,
                  descExame: element.descExame,
                })
                .then((e) => {
                  //se tudo correr bem faz update na BD
                  this.updatePedido(element)
                    .then((e) => {})
                    .catch((e) => console.log(e));
                })
                .catch((e) => {
                  console.log(e);
                });
            }
          }
        });
        if (contem == 0) {
          //senão contiver o pedido adiciona o que falta
          axios
            .post("http://localhost:3002/pedidos", {
              id: element.id,
              estado: element.estado,
              data: element.data,
              codigoExame: element.codigoExame,
              descExame: element.descExame,
            })
            .then((e) => {
              // se tudo corrr bem inserir na bd também
              this.insertPedido(element)
                .then((e) => {})
                .catch((e) => console.log(e));
            })
            .catch((err) => console.log(err));
        }
      });
    })

    .catch((e) => {
      console.log(e);
    });
};

module.exports.insertPedido = async (pedido) => {
  if (!pedido.relatorio) pedido.relatorio = "null";
  return await executaQuery(
    "insert into Pedido(idPedido,mensagem, data, estado, relatorio, codigoExame, descExame ) values(" +
      pedido.id +
      ",null,'" +
      pedido.data +
      "','" +
      pedido.estado +
      "','" +
      pedido.relatorio +
      "','" +
      pedido.codigoExame +
      "','" +
      pedido.descExame +
      "')"
  );
};

module.exports.updatePedido = async (pedido) => {
  if (!pedido.relatorio) pedido.relatorio = "null";
  return await executaQuery(
    "Update Pedido set mensagem=null, data='" +
      pedido.data +
      "', estado='" +
      pedido.estado +
      "', relatorio=" +
      pedido.relatorio +
      ", codigoExame='" +
      pedido.codigoExame +
      "', descExame='" +
      pedido.descExame +
      "' where idPedido=" +
      pedido.id
  );
};

const executaQuery = (query) => {
  return new Promise((resove, reject) => {
    let connection = mysql.createConnection({
      host: "localhost",
      user: "root",
      password: "password",
      database: "clinica",
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
