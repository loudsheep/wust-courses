import json
import matplotlib.pyplot as plt
import argparse
import os
import sys

def load_data(filepath):
    try:
        with open(filepath, 'r') as f:
            return json.load(f)
    except FileNotFoundError:
        print(f"Error: File '{filepath}' not found.")
        sys.exit(1)
    except json.JSONDecodeError:
        print(f"Error: Failed to decode JSON from '{filepath}'.")
        sys.exit(1)

def plot_progress(data, filename):
    history = data.get('history', [])
    if not history:
        print("Warning: No history data found for progression plot.")
        return

    iterations = [entry['iter'] for entry in history]
    fitness = [entry['fitness'] for entry in history]

    plt.figure(figsize=(10, 6))
    plt.plot(iterations, fitness, linestyle='-', color='b', linewidth=2, label='Best Fitness')
    
    total_iters = iterations[-1] if iterations else 0
    plt.title(f"Optimization Progress: {data.get('instance', 'Unknown')} (Total Iterations: {total_iters})", fontsize=14)
    plt.xlabel("Iteration", fontsize=12)
    plt.ylabel("Cost / Distance", fontsize=12)
    plt.grid(True, linestyle='--', alpha=0.7)
    plt.legend()
    
    plt.savefig(filename, dpi=300)
    plt.close()
    print(f"Saved progression plot to: {filename}")

def plot_solution(data, solution, filename):
    nodes = data.get('nodes', [])
    depot = data.get('depot', {})
    
    node_map = {n['id']: (n['x'], n['y']) for n in nodes}
    
    if depot.get('id') not in node_map:
        node_map[depot['id']] = (depot['x'], depot['y'])

    depot_id = depot.get('id')
    depot_coords = node_map[depot_id]

    plt.figure(figsize=(10, 10))
    
    plt.scatter(depot_coords[0], depot_coords[1], c='red', marker='s', s=150, label='Depot', zorder=3, edgecolor='black')

    routes = solution.get('routes', [])
    cmap = plt.cm.get_cmap('tab20')

    for idx, route in enumerate(routes):
        color = cmap(idx % 20)
        
        path_ids = [depot_id] + route + [depot_id]
        
        path_x = []
        path_y = []
        for pid in path_ids:
            if pid in node_map:
                path_x.append(node_map[pid][0])
                path_y.append(node_map[pid][1])
        
        plt.plot(path_x, path_y, color=color, linewidth=2, alpha=0.7, zorder=1, label=f'Route {idx+1}')
        
        route_params = [(node_map[pid][0], node_map[pid][1]) for pid in route if pid in node_map]
        if route_params:
            rx, ry = zip(*route_params)
            plt.scatter(rx, ry, color=color, s=50, zorder=2, edgecolor='black', linewidth=0.5)

            for pid in route:
                if pid in node_map:
                    px, py = node_map[pid]
                    plt.annotate(str(pid), (px, py), textcoords="offset points", xytext=(0, 5), ha='center', fontsize=8, weight='bold')

        for i in range(len(path_x) - 1):
            p1 = (path_x[i], path_y[i])
            p2 = (path_x[i+1], path_y[i+1])
            
            mid_x = (p1[0] + p2[0]) / 2
            mid_y = (p1[1] + p2[1]) / 2
            
            plt.annotate('', xy=(mid_x, mid_y), xytext=p1,
                         arrowprops=dict(arrowstyle='->', color=color, lw=1.5, shrinkA=0, shrinkB=0),
                         zorder=1)

    plt.title(f"Solution Rank #{solution['rank']} (Cost: {solution['fitness']:.2f})", fontsize=14)
    plt.xlabel("X Coordinate")
    plt.ylabel("Y Coordinate")
    plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left', borderaxespad=0.)
    plt.grid(True, linestyle=':', alpha=0.6)
    plt.tight_layout()

    plt.savefig(filename, dpi=300)
    plt.close()
    print(f"Saved solution map to: {filename}")

def main():
    parser = argparse.ArgumentParser(description="Visualize VRP Genetic Algorithm Results")
    parser.add_argument("--file", type=str, default="../mini_projekt/mini_projekt/results.json", help="Path to the .json results file")
    parser.add_argument("--top", type=int, default=1, help="Number of top solutions to visualize (default: 1)")
    
    args = parser.parse_args()

    data = load_data(args.file)
    instance_name = data.get("instance", "problem")

    output_dir = "data"
    os.makedirs(output_dir, exist_ok=True)

    progress_filename = os.path.join(output_dir, f"{instance_name}_progress.png")
    plot_progress(data, progress_filename)

    solutions = data.get("solutions", [])
    
    limit = min(args.top, len(solutions))
    
    for i in range(limit):
        sol = solutions[i]
        rank = sol.get("rank", i + 1)
        sol_filename = os.path.join(output_dir, f"{instance_name}_solution_{rank}.png")
        plot_solution(data, sol, sol_filename)

if __name__ == "__main__":
    main()