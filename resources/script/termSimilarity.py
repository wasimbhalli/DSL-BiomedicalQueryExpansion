import math
import sys
import os

if len(sys.argv) != 3: 
    print "Please specify two terms as argument!";
    sys.exit()

def cosine_similarity(v1,v2):
    sumxx, sumxy, sumyy = 0, 0, 0
    for i in range(len(v1)):
        x = v1[i]; y = v2[i]
        sumxx += x*x
        sumyy += y*y
        sumxy += x*y
    return sumxy/math.sqrt(sumxx*sumyy)

# Create a vocabulary using all the terms
vocabulary = {}
with open(os.path.abspath('data/WordEmbeddings/types.txt')) as fp:
    for i, line in enumerate(fp):
        vocabulary[line.rstrip()] = i

term1 = sys.argv[1]
term2 = sys.argv[2]

if term1 not in vocabulary or term2 not in vocabulary: 
    print 0
    sys.exit()

term1_index = vocabulary[term1]
term2_index = vocabulary[term2]    

highest_index = term1_index
if term2_index > term1_index:
    highest_index = term2_index
  
v1 = []
v2 = []
    
    
with open(os.path.abspath('data/WordEmbeddings/vectors.txt')) as fp:
    for i, line in enumerate(fp):
        if i == term1_index:
            for word in line.rstrip().split():
                 v1.append(float(word))
        elif i == term2_index:
            for word in line.rstrip().split():
                 v2.append(float(word))
        elif i > highest_index:
            break

print cosine_similarity(v1,v2)
            