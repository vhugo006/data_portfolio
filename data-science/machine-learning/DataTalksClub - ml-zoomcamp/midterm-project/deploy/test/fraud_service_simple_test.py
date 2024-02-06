import pickle
import numpy as np
import os

def predict_single(transaction, dv, model):
    X = dv.transform([transaction])
    y_pred = model.predict_proba(X)[:, 1]
    return y_pred[0]

current_dir = os.path.dirname(os.path.abspath(__file__))
file_path = os.path.join(current_dir, 'fraud-model.bin')

with open(file_path, 'rb') as f_in:
    dv, model = pickle.load(f_in)


transaction = {'v1': -0.3824536724413046,
               'v2': -0.6534625672206618,
               'v3': 0.8352720618781783,
               'v4': -1.5471578843263107,
               'v5': 0.0500077217465682,
               'v6': 0.1630272726361537,
               'v7': 0.6205743344174932,
               'v8': -0.0828801756451547,
               'v9': 0.9305169526698518,
               'v10': 0.0286103551642208,
               'v11': -0.2244123082230974,
               'v12': 2.226336494239908,
               'v13': 0.7358634990611479,
               'v14': 0.8366933565890834,
               'v15': -0.3573591582986628,
               'v16': 0.4236399968353382,
               'v17': 0.275668658488325,
               'v18': 0.8485622415233598,
               'v19': 0.2299698212524975,
               'v20': -0.3388968838015855,
               'v21': -0.0267060990734639,
               'v22': 0.4455309614243704,
               'v23': -0.4033949875812739,
               'v24': 0.2106312998045563,
               'v25': -0.1231727111585924,
               'v26': -2.2008062378707747,
               'v27': -0.0479554896948239,
               'v28': -0.613985459850345,
               'amount': 19233.41}


prediction = predict_single(transaction, dv, model)

print('prediction: %.3f' % prediction)
print('Is fraud?')
       
if prediction >= 0.98:
    print('Yes')
else:
    print('No')