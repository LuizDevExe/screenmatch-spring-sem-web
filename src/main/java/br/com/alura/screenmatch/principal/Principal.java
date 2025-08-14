package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner LEITURA = new Scanner(System.in);
    private final ConsumoApi CONSUMO = new ConsumoApi();
    private final ConverteDados CONVERSOR = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu(){
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = LEITURA.nextLine();
        var json = CONSUMO.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = CONVERSOR.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i<=dados.totalTemporadas(); i++){
            json = CONSUMO.obterDados(ENDERECO + nomeSerie.replace(" ", "+") +"&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = CONVERSOR.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

    //        for(int i = 0; i < dados.totalTemporadas(); i++){
    //            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
    //            for(int j = 0; j< episodiosTemporada.size(); j++){
    //                System.out.println(episodiosTemporada.get(j).titulo());
    //            }
    //        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));


//        List<String> nomes = Arrays.asList("Luiz", "Mariana", "Vitória", "Andreia");
//
//        nomes.stream()
//                .sorted()
//                .limit(3)
//                .filter(n -> n.startsWith("M"))
//                .map(n -> n.toUpperCase())
//                .forEach(System.out::println);


        // Cria uma lisga com o tipo de dado episódio e acessa o fluxo de informações dentro de temporadas
        List<DadosEpisodio> listaEpisodios = temporadas.stream()
                // Informa que quer acessar uma lista dentro de uma lista assim pegando todos os eps da série
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());


        listaEpisodios.stream()
                // Filtra os dados excluindo os quais tem N/A como dado de avaliação
                .filter(e -> ! e.avaliacao().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Primeiro filtro" + e))
                // Faz uma ordem descrecente dos dados
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                // Pega os 5 primeiros valores
                .limit(5)
                // Faz um print para cada um que restou
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap( t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);
//
//
//        // Filtrando episódios por ano d lançamento
//
//        // Escaneando ano que o usuário deseja
//        System.out.println("Qual ano de lançamento você gostaria?");
//
//        var ano = LEITURA.nextInt();
//        LEITURA.nextLine();
//
//        // Fazendo um formatador para formatar a data em padrão pt br
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        // Criando data local para comparação
//        LocalDate dataLancamento = LocalDate.of(ano,1,1);


        // Aproveitando a lista de episódios que já está formatada
//        episodios.stream()
//                // Filtrando episódios que não são num e com ano maior que o datalancamento
//                .filter(e -> e.getDataLancamento() != null &&
//                        e.getDataLancamento().getYear() == dataLancamento.getYear())
//                .forEach(e -> System.out.println(
//                    "Temporada: " + e.getTemporada() +
//                            " Episodio: " + e.getTitulo() +
//                            // Formatando data com nosso formater
//                            " Data Lançamento: " + e.getDataLancamento().format(formatter)
//                ));
    }
}