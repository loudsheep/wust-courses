import json
import matplotlib.pyplot as plt
import argparse
import os
import sys
import math

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

def verify_solution_cost(data, solution):
    nodes = data.get('nodes', [])
    depot = data.get('depot', {})
    capacity = data.get('capacity', float('inf'))
    
    node_map = {n['id']: {'coords': (n['x'], n['y']), 'demand': n.get('demand', 0)} for n in nodes}
    
    if depot.get('id') not in node_map:
        node_map[depot['id']] = {'coords': (depot['x'], depot['y']), 'demand': 0}
        
    depot_id = depot.get('id')
    routes = solution.get('routes', [])
    
    calculated_cost = 0.0
    
    for route in routes:
        path_ids = [depot_id]
        current_load = 0
        
        for width_node_id in route:
            demand = node_map.get(width_node_id, {}).get('demand', 0)
            if current_load + demand > capacity:
                path_ids.append(depot_id)
                current_load = 0
            current_load += demand
            path_ids.append(width_node_id)
            
        path_ids.append(depot_id)
        
        for i in range(len(path_ids) - 1):
            u_id = path_ids[i]
            v_id = path_ids[i+1]
            
            if u_id in node_map and v_id in node_map:
              p1 = node_map[u_id]['coords']
              p2 = node_map[v_id]['coords']
              
              exact_dist = math.sqrt((p1[0] - p2[0])**2 + (p1[1] - p2[1])**2)
              
              dist = int(round(exact_dist))
              
              calculated_cost += dist
            else:
                print(f"Warning: Missing coordinates for node {u_id} or {v_id}")

    reported_cost = solution.get('fitness', 0.0)
    diff = abs(calculated_cost - reported_cost)
    
    print(f"--- Verification for Solution Rank #{solution.get('rank', '?')} ---")
    print(f"Rounded Cost: {calculated_cost:.4f}")
    print(f"Reported Cost:   {reported_cost:.4f}")
    if diff < 1e-3:
        print("Status: MATCH")
    else:
        print(f"Status: MISMATCH (Diff: {diff:.4f})")
    print("---------------------------------------------")

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
    capacity = data.get('capacity', float('inf'))
    
    node_map = {n['id']: {'coords': (n['x'], n['y']), 'demand': n.get('demand', 0)} for n in nodes}
    
    if depot.get('id') not in node_map:
        node_map[depot['id']] = {'coords': (depot['x'], depot['y']), 'demand': 0}

    depot_id = depot.get('id')
    depot_coords = node_map[depot_id]['coords']

    plt.figure(figsize=(10, 10))
    
    plt.scatter(depot_coords[0], depot_coords[1], c='red', marker='s', s=150, label='Depot', zorder=3, edgecolor='black')

    routes = solution.get('routes', [])
    try:
        cmap = plt.get_cmap('tab20')
    except AttributeError:
        cmap = plt.cm.get_cmap('tab20')

    for idx, route in enumerate(routes):
        color = cmap(idx % 20)
        
        path_ids = [depot_id]
        current_load = 0
        
        for pid in route:
            demand = node_map.get(pid, {}).get('demand', 0)
            if current_load + demand > capacity:
                path_ids.append(depot_id)
                current_load = 0
            current_load += demand
            path_ids.append(pid)
        path_ids.append(depot_id)
        
        path_x = []
        path_y = []
        for pid in path_ids:
            if pid in node_map:
                coords = node_map[pid]['coords']
                path_x.append(coords[0])
                path_y.append(coords[1])
        
        plt.plot(path_x, path_y, color=color, linewidth=2, alpha=0.7, zorder=1, label=f'Route {idx+1}')
        
        route_params = []
        for pid in route:
            if pid in node_map:
                route_params.append(node_map[pid]['coords'])

        if route_params:
            rx, ry = zip(*route_params)
            plt.scatter(rx, ry, color=color, s=50, zorder=2, edgecolor='black', linewidth=0.5)

            for pid in route:
                if pid in node_map:
                    px, py = node_map[pid]['coords']
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
        
        verify_solution_cost(data, sol)

        rank = sol.get("rank", i + 1)
        sol_filename = os.path.join(output_dir, f"{instance_name}_solution_{rank}.png")
        plot_solution(data, sol, sol_filename)

if __name__ == "__main__":
    main()