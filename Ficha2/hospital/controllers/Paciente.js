var mysql = require("mysql");

module.exports.getPacientes = async () => {
  return await executaQuery("SELECT * FROM Paciente");
};

module.exports.getPaciente = async (id) => {
  return JSON.parse(
    JSON.stringify(
      await executaQuery("SELECT * FROM Paciente where idPaciente=" + id)
    )
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
