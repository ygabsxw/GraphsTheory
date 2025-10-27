import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
import numpy as np

# --- Graficos e codigo gerados pelo Gemini Pro ---


# Colunas:
# 'V': Graph Size (Vertices)
# 'Time (ms)': Execution Time (milliseconds)
# 'Weight': Total Weight (comprimento)
# 'Edges': Total Edges (do caminho)
# -----------------------------------------------------------------

data_tipo_1 = [
    # Tree graph (1000 vertices) - Run 2
    {'V': 1000, 'Time (ms)': 0.977229, 'Weight': 27, 'Edges': 5},
    # Tree graph (10000 vertices) - Run 2
    {'V': 10000, 'Time (ms)': 0.576937, 'Weight': 27, 'Edges': 4},
    # Tree graph (100000 vertices) - Run 2
    {'V': 100000, 'Time (ms)': 11.357670, 'Weight': 25, 'Edges': 5},
    # Tree graph (500000 vertices) - Run 1
    {'V': 500000, 'Time (ms)': 14.956662, 'Weight': 36, 'Edges': 7},
]

data_tipo_2 = [
    # Euler graph (1000 vertices) - Run 2
    {'V': 1000, 'Time (ms)': 3.360708, 'Weight': 15, 'Edges': 6},
    # Euler graph (10000 vertices) - Run 2
    {'V': 10000, 'Time (ms)': 4.279502, 'Weight': 27, 'Edges': 9},
    # Euler graph (100000 vertices) - Run 2
    {'V': 100000, 'Time (ms)': 45.528802, 'Weight': 31, 'Edges': 8},
    # Euler graph (500000 vertices) - Run 2
    {'V': 500000, 'Time (ms)': 284.472843, 'Weight': 50, 'Edges': 16},
]

# Nomes para as legendas
NOME_TIPO_1 = "Tree Graph (Grafo Árvore)"
NOME_TIPO_2 = "Euler Graph (Grafo Euleriano)"

# Nome do arquivo de saída
PDF_FILENAME = "Relatorio_Analise_Grafos.pdf"


# --- Fim da área de dados ---
# (Não precisa editar abaixo desta linha)


def create_table_page(df, title, pdf_pages):
    """
    Função auxiliar para criar uma página de tabela no PDF.
    """
    # Reordenar colunas para um formato lógico
    cols = ['V', 'Time (ms)', 'Weight', 'Edges']
    df = df[cols]
    
    fig, ax = plt.subplots(figsize=(11, 4)) # Tamanho A4 paisagem
    ax.axis('tight')
    ax.axis('off')
    
    # Criar a tabela
    tabela = ax.table(cellText=df.values, colLabels=df.columns, loc='center', cellLoc='center')
    tabela.auto_set_font_size(False)
    tabela.set_fontsize(10)
    tabela.scale(1.2, 1.2) # Ajusta o tamanho da tabela

    # Formatar o header
    for (i, j), cell in tabela.get_celld().items():
        if i == 0:
            cell.set_text_props(weight='bold', color='white')
            cell.set_facecolor('#40466e')
        else:
            cell.set_facecolor('white')

    plt.title(title, fontsize=16, y=1.1)
    plt.tight_layout()
    pdf_pages.savefig(fig, bbox_inches='tight')
    plt.close()
    print(f"Página de Tabela '{title}' gerada.")

def create_plot_page(dfs, titles, pdf_pages, title, x_col='V', y_col='Time (ms)', log_scale=False):
    """
    Função auxiliar para criar uma página de gráfico no PDF.
    """
    fig, ax = plt.subplots(figsize=(10, 6))
    
    for df, label in zip(dfs, titles):
        if not df.empty:
            # Ordenar pelos vértices para a linha sair correta
            df_sorted = df.sort_values(by=x_col)
            ax.plot(df_sorted[x_col], df_sorted[y_col], marker='o', linestyle='-', label=label)

    ax.set_xlabel('Número de Vértices (V)')
    ax.set_ylabel('Tempo de Execução (ms)')
    ax.set_title(title, fontsize=16)
    ax.grid(True, which="both", ls="--", c='0.7')
    
    if log_scale:
        ax.set_yscale('log')
        ax.set_xscale('log')
        ax.set_xlabel('Número de Vértices (V) - Escala Log')
        ax.set_ylabel('Tempo de Execução (ms) - Escala Log')
    else:
        # Formatar eixos para escala linear para não usar notação científica
        ax.get_xaxis().set_major_formatter(plt.FuncFormatter(lambda x, p: format(int(x), ',')))
        ax.get_yaxis().set_major_formatter(plt.FuncFormatter(lambda x, p: format(float(x), '.2f')))


    if len(dfs) > 1:
        ax.legend()
        
    plt.tight_layout()
    pdf_pages.savefig(fig)
    plt.close()
    print(f"Página de Gráfico '{title}' gerada.")

def main():
    # Converter dados em DataFrames pandas
    df1 = pd.DataFrame(data_tipo_1)
    df2 = pd.DataFrame(data_tipo_2)

    if df1.empty or df2.empty:
        print("ERRO: Os dados 'data_tipo_1' ou 'data_tipo_2' estão vazios.")
        return

    # Inicia o arquivo PDF
    with PdfPages(PDF_FILENAME) as pdf:
        
        # --- Página 1: Tabela Tipo 1 ---
        create_table_page(df1, f"Resultados de Eficácia e Eficiência\n{NOME_TIPO_1}", pdf)
        
        # --- Página 2: Tabela Tipo 2 ---
        create_table_page(df2, f"Resultados de Eficácia e Eficiência\n{NOME_TIPO_2}", pdf)

        # --- Página 3: Gráfico Eficiência Tipo 1 ---
        create_plot_page([df1], [NOME_TIPO_1], pdf,
                         f"Eficiência - {NOME_TIPO_1}\n(Vértices vs. Tempo)")
                         
        # --- Página 4: Gráfico Eficiência Tipo 2 ---
        create_plot_page([df2], [NOME_TIPO_2], pdf,
                         f"Eficiência - {NOME_TIPO_2}\n(Vértices vs. Tempo)")

        # --- Página 5: Gráfico Comparativo (Escala Linear) ---
        create_plot_page([df1, df2], [NOME_TIPO_1, NOME_TIPO_2], pdf,
                         "Comparativo de Eficiência (Escala Linear)")

        # --- Página 6: Gráfico Comparativo (Escala Log-Log) ---
        create_plot_page([df1, df2], [NOME_TIPO_1, NOME_TIPO_2], pdf,
                         "Comparativo de Eficiência (Escala Log-Log)",
                         log_scale=True)
                         
    print(f"\nRelatório '{PDF_FILENAME}' gerado com sucesso!")

if __name__ == "__main__":
    main()