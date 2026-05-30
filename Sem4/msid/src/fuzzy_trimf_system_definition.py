import skfuzzy as fuzz
import numpy as np
from skfuzzy import control as ctrl

social_media_while_eating = ctrl.Antecedent(
    np.linspace(0, 3, 500), "social_media_while_eating"
)
overeating_level = ctrl.Antecedent(np.linspace(0, 12, 1000), "overeating_level")
employment_status = ctrl.Antecedent(np.linspace(0, 4, 500), "employment_status")
low_energy = ctrl.Antecedent(np.linspace(0, 2, 500), "low_energy")

depression_type = ctrl.Consequent(np.linspace(0, 11, 1000), "depression_type")

social_media_while_eating["never"] = fuzz.trimf(
    social_media_while_eating.universe, [0, 0, 0]
)
social_media_while_eating["rarely"] = fuzz.trimf(
    social_media_while_eating.universe, [1, 1, 1]
)
social_media_while_eating["often"] = fuzz.trimf(
    social_media_while_eating.universe, [2, 2, 2]
)
social_media_while_eating["always"] = fuzz.trimf(
    social_media_while_eating.universe, [3, 3, 3]
)

overeating_level["none"] = fuzz.trimf(overeating_level.universe, [0, 0, 2])
overeating_level["mild"] = fuzz.trimf(overeating_level.universe, [1, 3, 6])
overeating_level["moderate"] = fuzz.trimf(overeating_level.universe, [4, 6.5, 9])
overeating_level["severe"] = fuzz.trimf(overeating_level.universe, [7, 10, 12])

employment_status["unemployed"] = fuzz.trimf(employment_status.universe, [0, 0, 0])
employment_status["student"] = fuzz.trimf(employment_status.universe, [1, 1, 1])
employment_status["employed"] = fuzz.trimf(employment_status.universe, [2, 2, 2])
employment_status["self-employed"] = fuzz.trimf(employment_status.universe, [3, 3, 3])
employment_status["other"] = fuzz.trimf(employment_status.universe, [4, 4, 4])

low_energy["no"] = fuzz.trimf(low_energy.universe, [0, 0, 0.5])
low_energy["yes"] = fuzz.trimf(low_energy.universe, [0.5, 1, 1.5])
low_energy["sometimes"] = fuzz.trimf(low_energy.universe, [1.5, 2, 2])

depression_type["result_0"] = fuzz.trimf(depression_type.universe, [0, 0, 0.5])
depression_type["result_1_2"] = fuzz.trimf(depression_type.universe, [1, 1.5, 2])
depression_type["result_3_4"] = fuzz.trimf(depression_type.universe, [3, 3.5, 4])
depression_type["result_5"] = fuzz.trimf(depression_type.universe, [4.5, 5, 5.5])
depression_type["result_6_7"] = fuzz.trimf(depression_type.universe, [6, 6.5, 7])
depression_type["result_8_9"] = fuzz.trimf(depression_type.universe, [8, 8.5, 9])
depression_type["result_10_11"] = fuzz.trimf(depression_type.universe, [10, 10.5, 11])

rules = [
    ctrl.Rule(
        social_media_while_eating["always"] & overeating_level["severe"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & overeating_level["severe"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & overeating_level["severe"],
        depression_type["result_0"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & overeating_level["severe"],
        depression_type["result_6_7"],
    ),
    ctrl.Rule(
        social_media_while_eating["always"] & overeating_level["severe"],
        depression_type["result_6_7"],
    ),
    ctrl.Rule(
        social_media_while_eating["always"] & overeating_level["severe"],
        depression_type["result_1_2"],
    ),
    ctrl.Rule(
        social_media_while_eating["never"] & overeating_level["severe"],
        depression_type["result_6_7"],
    ),
    ctrl.Rule(
        social_media_while_eating["always"] & overeating_level["moderate"],
        depression_type["result_1_2"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & overeating_level["severe"],
        depression_type["result_6_7"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & overeating_level["moderate"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        overeating_level["severe"] & low_energy["yes"],
        depression_type["result_10_11"],
    ),
    ctrl.Rule(
        overeating_level["severe"] & low_energy["yes"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        overeating_level["severe"]
        & low_energy["yes"]
        & social_media_while_eating["never"],
        depression_type["result_5"],
    ),
    ctrl.Rule(
        overeating_level["severe"]
        & low_energy["yes"]
        & social_media_while_eating["never"],
        depression_type["result_5"],
    ),
    ctrl.Rule(
        overeating_level["severe"]
        & low_energy["yes"]
        & social_media_while_eating["often"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        overeating_level["severe"]
        & low_energy["yes"]
        & social_media_while_eating["never"],
        depression_type["result_5"],
    ),
    ctrl.Rule(
        overeating_level["severe"]
        & low_energy["yes"]
        & social_media_while_eating["often"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        overeating_level["severe"] & low_energy["no"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        overeating_level["severe"] & low_energy["no"],
        depression_type["result_5"],
    ),
    ctrl.Rule(
        overeating_level["none"] & low_energy["yes"],
        depression_type["result_1_2"],
    ),
    ctrl.Rule(
        overeating_level["severe"] & low_energy["yes"],
        depression_type["result_5"],
    ),
    ctrl.Rule(
        overeating_level["severe"] & low_energy["yes"],
        depression_type["result_3_4"],
    ),
    ctrl.Rule(
        overeating_level["mild"] & low_energy["yes"],
        depression_type["result_5"],
    ),
    ctrl.Rule(
        overeating_level["moderate"] & low_energy["yes"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        overeating_level["severe"] & low_energy["no"],
        depression_type["result_1_2"],
    ),
    ctrl.Rule(
        social_media_while_eating["always"] & low_energy["yes"],
        depression_type["result_1_2"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & low_energy["yes"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        social_media_while_eating["always"] & low_energy["yes"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & low_energy["yes"],
        depression_type["result_1_2"],
    ),
    ctrl.Rule(
        social_media_while_eating["always"] & low_energy["yes"],
        depression_type["result_5"],
    ),
    ctrl.Rule(
        social_media_while_eating["always"] & low_energy["no"],
        depression_type["result_6_7"],
    ),
    ctrl.Rule(
        social_media_while_eating["always"] & low_energy["no"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & low_energy["no"],
        depression_type["result_5"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & low_energy["no"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        social_media_while_eating["rarely"] & low_energy["yes"],
        depression_type["result_8_9"],
    ),
    ctrl.Rule(
        social_media_while_eating["often"] & low_energy["yes"],
        depression_type["result_1_2"],
    ),
]

depression_ctrl = ctrl.ControlSystem(rules)
depression_sim = ctrl.ControlSystemSimulation(depression_ctrl)
