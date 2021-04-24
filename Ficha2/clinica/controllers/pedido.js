var mysql = require("mysql");

module.exports.insertPedido = async (pedido) => {
  return await executaQuery(
    "insert into Pedido(mensagem, data, estado, relatorio, codigoExame, descExame ) values('" +
      pedido.mensagem +
      "','" +
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
