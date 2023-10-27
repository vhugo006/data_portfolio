import pickle
import numpy as np
import os

from flask import Flask, request, jsonify


def predict_single(transaction, dv, model):
    X = dv.transform([transaction])
    y_pred = model.predict_proba(X)[:, 1]
    return y_pred[0]


current_dir = os.path.dirname(os.path.abspath(__file__))
file_path = os.path.join(current_dir, 'fraud-model.bin')

with open(file_path, 'rb') as f_in:
    dv, model = pickle.load(f_in)


app = Flask('fraud')


@app.route('/predict', methods=['POST'])
def predict():
    transaction = request.get_json()

    prediction = predict_single(transaction, dv, model)

    result = {
        'is_fraud': bool(prediction > 0.97)
    }

    return jsonify(result)


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=9696)
