#!/bin/bash

read -p "Jaki jest twój ulubiony system operacyjny? " os

if [[ "$os" == "Windows" ]]; then
    echo "Cóż, każdy ma swoje gusta. Nawet jeśli są trochę przestarzałe."
elif [[ "$os" == "Mac" ]]; then
    echo "Bo kto nie chciałby płacić więcej za mniej możliwości?"
elif [[ "$os" == "Linux" ]]; then
    echo "Świetny wybór!"
else
    echo "Czy $os jest systemem operacyjnym?"
fi