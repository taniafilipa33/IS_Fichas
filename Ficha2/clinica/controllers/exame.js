var mysql = require("mysql");

module.exports.insertExame = async (exame) => {
  return await executaQuery(
    "insert into Exame(codigo_ato, ato, id_externo, medico, Paciente_idPaciente, Pedido_idPedido ) values('" +
      exame.codigo_ato +
      "','" +
      exame.ato +
      "',1,'" +
      exame.medico +
      "'," +
      exame.Paciente_idPaciente +
      "," +
      exame.Pedido_idPedido +
      ")"
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
