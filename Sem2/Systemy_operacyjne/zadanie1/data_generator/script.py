import random
import matplotlib.pyplot as plt
from collections import Counter
import numpy as np

def generateNormalDistribution(minExec, maxExec, maxRegister, count):
    norm = np.random.normal(loc=maxRegister / 2, scale=maxRegister / 10, size=count)
    res = []
    for i in norm:
        registerTime = max(0, min(maxRegister, int(i)))  # Keep within bounds
        res.append((random.randint(minExec, maxExec), registerTime))
    return res

def generateFullyRandom(minExec, maxExec, maxRegister, count):
    res =[]
    for _ in range(count):
        # execTime = random.randint(minExec, maxExec)
        execTime = int(np.random.uniform(minExec, maxExec, 1)[0])
        registerTime = random.randint(0, maxRegister)
        res.append((execTime, registerTime))
    return res

def generateBimodalDistribution(minExec, maxExec, maxRegister, count):
    half_count = count // 2
    norm1 = np.random.normal(loc=maxRegister * 0.2, scale=maxRegister * 0.07, size=half_count)
    norm2 = np.random.normal(loc=maxRegister * 0.8, scale=maxRegister * 0.07, size=count - half_count)
    combined = np.concatenate((norm1, norm2))
    res = []
    for i in combined:
        registerTime = max(0, min(maxRegister, int(i)))  # Keep within bounds
        res.append((random.randint(minExec, maxExec), registerTime))
    return res





def parseDataToPlot(data):
    # Extract only the registeredAt values
    registered_at_values = [entry[1] for entry in data]
    
    # Count occurrences of each registeredAt value
    counts = Counter(registered_at_values)
    
    # Get distinct registeredAt values sorted (optional, for better plotting)
    distinct_registered_at = sorted(counts.keys())
    
    # Get corresponding counts
    counts_list = [counts[value] for value in distinct_registered_at]
    
    return distinct_registered_at, counts_list

def parseToPlotXY(data):
    a1, a2 = [], []
    for i in data:
        a1.append(i[0])
        a2.append(i[1])
    return a1, a2
    

def plotData(data, title):
    a1, a2 = parseToPlotXY(data)
    plt.title(title)
    plt.scatter(a1, a2, s=1)
    plt.show()

def savePlot(data, title, file):
    a1, a2 = parseToPlotXY(data)
    plt.title(title)
    plt.scatter(a1, a2, s=1)
    plt.savefig(file)


def main():
    minExec = 2
    maxExec = 100
    count = 10_000
    maxRegister = 20_000

    #data = generateFullyRandom(minExec, maxExec, maxRegister, count)
    #plotData(data, "Fully random")
    
    #data = generateNormalDistribution(minExec, maxExec, maxRegister, count)
    #plotData(data, "Normal distribution")

    data = generateBimodalDistribution(minExec, maxExec, maxRegister, count)
    savePlot(data, "Bimodal distribution", "data/bimodal2.png")



if __name__=="__main__":
    main()
