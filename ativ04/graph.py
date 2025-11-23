import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

# Dados extraídos dos logs fornecidos
data = [
    # Euler Graphs
    {'Type': 'Euler', 'Vertices': 1000, 'Time_ms': 2.8266, 'Paths': 5},
    {'Type': 'Euler', 'Vertices': 10000, 'Time_ms': 14.2222, 'Paths': 4},
    {'Type': 'Euler', 'Vertices': 10000, 'Time_ms': 2.0314, 'Paths': 2},
    {'Type': 'Euler', 'Vertices': 100000, 'Time_ms': 133.5850, 'Paths': 6},
    {'Type': 'Euler', 'Vertices': 100000, 'Time_ms': 63.6544, 'Paths': 3},
    {'Type': 'Euler', 'Vertices': 500000, 'Time_ms': 148.8526, 'Paths': 3},
    {'Type': 'Euler', 'Vertices': 500000, 'Time_ms': 34.9336, 'Paths': 1},
    
    # Layered Graphs
    {'Type': 'Layered', 'Vertices': 1000, 'Time_ms': 0.6661, 'Paths': 1},
    {'Type': 'Layered', 'Vertices': 1000, 'Time_ms': 2.5718, 'Paths': 1},
    {'Type': 'Layered', 'Vertices': 10000, 'Time_ms': 0.3668, 'Paths': 1},
    {'Type': 'Layered', 'Vertices': 10000, 'Time_ms': 6.4089, 'Paths': 1},
    {'Type': 'Layered', 'Vertices': 100000, 'Time_ms': 51.2026, 'Paths': 3},
    {'Type': 'Layered', 'Vertices': 100000, 'Time_ms': 41.5861, 'Paths': 3},
    {'Type': 'Layered', 'Vertices': 500000, 'Time_ms': 281.7062, 'Paths': 4},
    {'Type': 'Layered', 'Vertices': 500000, 'Time_ms': 136.7737, 'Paths': 4}
]

df = pd.DataFrame(data)

# --- 1. Calcular Média de Tempo por Tamanho e Tipo ---
avg_df = df.groupby(['Type', 'Vertices'])['Time_ms'].mean().reset_index()

# Separar dados para plotagem
euler_data = avg_df[avg_df['Type'] == 'Euler']
layered_data = avg_df[avg_df['Type'] == 'Layered']

# --- 2. Gerar Gráfico de Linha (Escala Logarítmica no X) ---
plt.figure(figsize=(10, 6))

plt.plot(euler_data['Vertices'], euler_data['Time_ms'], marker='o', label='Grafo Euleriano', linewidth=2)
plt.plot(layered_data['Vertices'], layered_data['Time_ms'], marker='s', label='Grafo em Camadas (Layered)', linewidth=2)

plt.xscale('log') # Escala logarítmica para visualizar melhor 1k -> 500k
plt.xlabel('Número de Vértices (Escala Log)')
plt.ylabel('Tempo Médio de Execução (ms)')
plt.title('Eficiência: Tempo de Execução vs Tamanho do Grafo')
plt.grid(True, which="both", ls="-", alpha=0.4)
plt.legend()

# Salvar gráfico
plt.savefig('grafico_eficiencia.png')
print("Gráfico 'grafico_eficiencia.png' gerado com sucesso.")

# --- 3. Exibir Tabelas no Console (Formatadas) ---
print("\n=== TABELA DE RESULTADOS: GRAFOS EULERIANOS ===")
print(df[df['Type'] == 'Euler'][['Vertices', 'Time_ms', 'Paths']].to_string(index=False))

print("\n=== TABELA DE RESULTADOS: GRAFOS EM CAMADAS (LAYERED) ===")
print(df[df['Type'] == 'Layered'][['Vertices', 'Time_ms', 'Paths']].to_string(index=False))

# --- 4. Exibir Resumo Estatístico ---
print("\n=== MÉDIAS DE TEMPO (ms) ===")
print(avg_df.pivot(index='Vertices', columns='Type', values='Time_ms'))