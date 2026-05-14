package com.dsastream;

import com.dsastream.client.Client;
import com.dsastream.model.Movie;
import com.dsastream.server.Server;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================");
        System.out.println("   INICIALIZANDO SISTEMA DE STREAMING...          ");
        System.out.println("==================================================");

        // Client and Server initialization
        Server server = new Server();
        server.loadInitialData();

        Client client = new Client();
        preloadCache(client, server); // Load 50 movies into client's cache

        boolean isRunning = true;

        // Main loop for user interaction
        while (isRunning) {
            printMenu();
            System.out.print("Escolha uma opção: ");

            try {
                int option = scanner.nextInt();

                switch (option) {
                    case 1:
                        interactiveSearch(scanner, client, server);
                        break;
                    case 2:
                        runTestBattery(client, server);
                        break;
                    case 3:
                        printAnalysis();
                        break;
                    case 0:
                        System.out.println("\n[SISTEMA] Encerrando a aplicação. Até logo!");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("\n[ERRO] Opção inválida. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n[ERRO] Por favor, digite apenas números.");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    // --- MÉTODOS DE INTERFACE ---

    private static void printMenu() {
        System.out.println("\n==================================================");
        System.out.println("               MENU PRINCIPAL                     ");
        System.out.println("==================================================");
        System.out.println("1. Buscar um Filme");
        System.out.println("2. Executar Bateria de 20 Consultas");
        System.out.println("3. Ler Análise dos Resultados");
        System.out.println("0. Sair");
        System.out.println("==================================================");
    }

    // --- MÉTODOS DE LÓGICA E REQUISITOS ---

    private static void preloadCache(Client client, Server server) {
        System.out.println("[SISTEMA] Pré-carregando 50 filmes no cache do Cliente...");
        for (int i = 1; i <= 50; i++) {
            Movie m = server.requestMovieWithIndex(i);
            if (m != null) {
                client.addToCache(m);
            }
        }
        System.out.println("[SISTEMA] Cache inicializado com sucesso!");
    }

    private static void interactiveSearch(Scanner scanner, Client client, Server server) {
        System.out.print("\nDigite o ID do filme que deseja assistir (1 a 1000): ");
        try {
            int targetId = scanner.nextInt();
            executeQuery(client, server, targetId, true);
        } catch (InputMismatchException e) {
            System.out.println("[ERRO] ID inválido. Digite um número inteiro.");
            scanner.nextLine();
        }
    }

    private static void runTestBattery(Client client, Server server) {
        System.out.println("\n==================================================");
        System.out.println("       INICIANDO BATERIA DE 20 CONSULTAS          ");
        System.out.println("==================================================\n");

        System.out.println(">>> ETAPA 1: 2 Consultas Inválidas ");
        executeQuery(client, server, 1005, true);
        executeQuery(client, server, -5, true);

        System.out.println("\n>>> ETAPA 2: 6 Consultas de registros no Cache (Hits)");
        int[] cacheIds = {5, 10, 20, 30, 40, 50};
        for (int id : cacheIds) executeQuery(client, server, id, true);

        System.out.println("\n>>> ETAPA 3: 6 Consultas SEM Indexação (Varredura na Lista)");
        int[] slowIds = {100, 150, 200, 250, 300, 350};
        for (int id : slowIds) executeQuery(client, server, id, false);

        System.out.println("\n>>> ETAPA 4: 6 Consultas COM Indexação (Tabela Hash)");
        int[] fastIds = {500, 600, 700, 800, 900, 1000};
        for (int id : fastIds) executeQuery(client, server, id, true);

        System.out.println("\n[SISTEMA] Bateria de testes concluída!");
    }


    private static void executeQuery(Client client, Server server, int searchId, boolean useIndex) {
        System.out.println("\n-> Buscando ID: " + searchId);

        // Tenta no Cache (Cliente)
        Movie movie = client.getCacheTree().search(searchId);

        if (movie == null) {
            // Tenta no Servidor (Miss)
            if (useIndex) {
                movie = server.requestMovieWithIndex(searchId);
            } else {
                movie = server.requestMovieWithoutIndex(searchId);
            }

            // Atualiza Cache e Exibe
            if (movie != null) {
                client.addToCache(movie);
            } else {
                System.out.println("[App] Erro 404: Filme não encontrado no catálogo do servidor.");
            }
        } else {
            System.out.println("[App] Reproduzindo '" + movie.getTitle() + "' a partir do Cache local!");
        }
    }

    private static void printAnalysis() {
        System.out.println("\n=== BREVE ANÁLISE DOS RESULTADOS ===");
        System.out.println("1. Cache (AVL): Eficiência O(log n). Buscas resultaram em ~5 comparações.");
        System.out.println("2. Servidor SEM Índice: Ineficiente O(n). Buscas resultaram em centenas de comparações.");
        System.out.println("3. Servidor COM Índice: Eficiência O(1). Tabela Hash resolveu com apenas 1 ou 2 comparações.");
        System.out.println("--------------------------------------------------");
    }
}