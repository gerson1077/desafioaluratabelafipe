package br.com.alura.TabelaFipe.princial;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoApi;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    private ConsumoApi consumo = new ConsumoApi();

    private ConverteDados conversor = new ConverteDados();

    public void ExibeMenu(){
        var menu = """
                *** OPÇÕES ***
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consultar: 
                """;

        System.out.println(menu);
        var opcao = leitura.nextLine();

        String endereco;

        //vai verificar parte da string digitada pelo usuário
        if (opcao.toLowerCase().contains("carr")){
            endereco = URL_BASE+"carros/marcas";
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE+"motos/marcas";
        } else {
            endereco = URL_BASE+"caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);

        //exibição das marcas
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("informa o código da marca para consulta: ");
        var codigoMarca = leitura.nextLine();

        endereco += "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);

        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca:");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\ndigite um trecho do nome do carro a ser buscado: ");
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o código do modelo para buscar os valores da FIPE: ");
        var codigomodelo = leitura.nextLine();

        endereco += "/" + codigomodelo + "/anos";
        json = consumo.obterDados(endereco);

        List<Dados> anos = conversor.obterLista(json, Dados.class);
        // é preciso agora criar um laço para percorrer a lista de anos,
        // busque o dado do carro e jogue numa lista
        List<Veiculo> veiculos = new ArrayList<>();

        //é preciso ainda atualizar o endereço da solicitação

        for (int i = 0; i < anos.size(); i++) {
                  var enderecoAnos = endereco + "/" + anos.get(i).codigo();
                  json = consumo.obterDados(enderecoAnos);
                  // popular a Record Veiculos
                  Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
                  //cada veículo encontrado será adicionado na lista
                  veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados por ano: ");
        veiculos.forEach(System.out::println);











    }
}
