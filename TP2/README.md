# Tutorial 2 - Kotlin Fundamentals & Cool Weather App (MIP-2)

**Course:** Engenharia Informática e de Computadores  
**Student:** Lucas Alemão  
**Date:** 16/04/2026  
**Repository URL:** https://github.com/Alemaozinho/CM_Git

---

## 1. Introduction
Este relatório descreve a implementação do Tutorial 2 da unidade curricular de Computação Móvel. O foco deste trabalho incide na exploração de funcionalidades avançadas da linguagem Kotlin e no desenvolvimento de uma aplicação Android dinâmica ("Cool Weather App"). O objetivo é demonstrar a capacidade de integrar serviços externos (APIs) e aplicar metodologias de planeamento estruturadas para o desenvolvimento de software móvel.

## 2. System Overview
O projeto atual contempla dois módulos principais:
* **Exercícios de Fundamentos de Kotlin:** Resolução de problemas técnicos que exploram processamento de logs, sistemas de cache com tipos genéricos, pipelines de dados e manipulação de vetores.
* **Aplicação Cool Weather (MIP-2):** Uma aplicação móvel que obtém previsões meteorológicas em tempo real com base em coordenadas geográficas, apresentando uma galeria horária dinâmica e adaptação visual a temas claro/escuro.

## 3. Architecture and Design
A solução foi desenhada para ser modular e eficiente:
* **Padrão de Arquitetura:** Foi adotado o padrão **MVVM** (Model-View-ViewModel) para a aplicação Android, facilitando a gestão de dados e a reatividade da interface.
* **Organização de Projeto:** O código fonte reside na pasta `/app`, enquanto a documentação de suporte e planeamento está centralizada na pasta `/docs`.
* **Design Patterns:** Uso de **Observer** para monitorizar alterações nos dados meteorológicos e **Adapter Pattern** para a gestão do RecyclerView.

## 4. Implementation
* **Lógica e POO:** Implementação rigorosa de `sealed classes`, `generics` e sobrecarga de operadores (`operator overloading`) nos exercícios de Kotlin.
* **Gestão de Rede:** Implementação de pedidos assíncronos à API Open-Meteo, garantindo que o processamento de rede ocorre fora da thread principal de UI.
* **Serialização:** Utilização da biblioteca **GSON** para o mapeamento eficiente entre o formato JSON recebido e as classes de dados Kotlin.

## 5. Testing and Validation
* **Validação Funcional:** Testes realizados com diferentes inputs de latitude/longitude para garantir a integridade dos dados da galeria.
* **Conectividade:** Verificação do comportamento da aplicação em situações de ausência de rede.
* **Interface:** Teste de adaptabilidade dos ícones e cores conforme o estado meteorológico e o tema definido pelo sistema.

## 6. Usage Instructions
* **Requisitos:** Android Studio (versão Ladybug ou superior) e ligação à internet para consulta de dados.
* **Configuração:** Abrir o projeto e sincronizar o Gradle (versão 8.4).
* **Execução:** Correr num emulador ou dispositivo físico (API 24+). Inserir as coordenadas desejadas e clicar no botão de atualização para visualizar a previsão horária.

---

# Autonomous Software Engineering Sections - only for [AC OK, AI OK]

## 7. Prompting Strategy
A interação com as ferramentas de IA baseou-se em fornecer o contexto das especificações técnicas criadas na fase de planeamento. Os prompts foram utilizados sobretudo para a estruturação de layouts XML complexos e para a resolução de conflitos de dependências no Gradle.

## 8. Autonomous Agent Workflow
O desenvolvimento seguiu o fluxo de "Planeamento-Primeiro". A IA atuou como um assistente de produtividade que auxiliou na tradução do plano de implementação para código estrutural, especialmente na criação do WeatherAdapter e na lógica de ligação à API.

## 9. Verification of AI-Generated Artifacts
Todos os componentes gerados com auxílio de IA foram validados através de:
* **Manual Review:** Revisão linha a linha para assegurar que o código seguia os padrões de codificação de Kotlin.
* **Debugging:** Utilização das ferramentas de inspeção do Android Studio para validar o fluxo de dados e a performance da lista.

## 10. Human vs AI Contribution
* **Human-Developed:** Toda a componente lógica da Secção 1 (Exercícios Kotlin), definição da arquitetura de pacotes e validação final de todos os componentes de rede e UI.
* **AI-Assisted:** Estruturação de partes do código do MIP-2 e apoio na redação técnica da documentação Markdown.

## 11. Ethical and Responsible Use
O uso de IA foi restringido ao que é permitido no enunciado (MIP-2). O aluno manteve o controlo total sobre o design da solução, utilizando as ferramentas apenas para otimizar a implementação técnica e documentação, sem comprometer a compreensão dos conceitos fundamentais.

---

# Development Process

## 12. Version Control and Commit History
O projeto utilizou Git para registar a evolução do desenvolvimento. O histórico de commits demonstra um progresso incremental, separando as fases de lógica inicial, criação da interface e integração final de dados.

## 13. Difficulties and Lessons Learned
* **Desafios:** Domínio do ciclo de vida das Activities e gestão de threading para evitar o bloqueio da interface.
* **Aprendizagem:** Consolidação da metodologia de planeamento prévio e melhor compreensão da importância da documentação técnica para projetos de engenharia.

## 14. Future Improvements
* Integração de cache local para persistência de dados.
* Adição de animações na transição dos itens da galeria meteorológica.

---

## 15. AI Usage Disclosure (Mandatory)
Relativamente aos exercícios da **Secção 1** (Kotlin Fundamentals), o desenvolvimento foi realizado de forma independente através de **estudo autónomo**, com recurso à documentação oficial, fóruns e tutoriais especializados no YouTube.

Para o marco **MIP-2** (Cool Weather App), declaro o uso do **Gemini (Google)** como assistente de planeamento e redação técnica, conforme permitido. Toda a lógica foi validada manualmente e a responsabilidade pelo conteúdo final é do aluno.
