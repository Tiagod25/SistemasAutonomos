import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import pyrebase
import pprint
from sklearn.cluster import MiniBatchKMeans

PERIOD = 3600 * 2 # 2 hours

config = {
  "apiKey": "AIzaSyDJD3mQl86l1KwPPu3RH8Ij6HCP5F3Ar-M",
  "authDomain": "in-dependence.firebaseapp.com",
  "databaseURL": "https://in-dependence.firebaseio.com",
  "storageBucket": "in-dependence.appspot.com"
}

def build_dataframe(message):
    if message['path'] == '/' and message['data'] != None: # chunk inicial
        registos = message['data']
        # build list of dictionaries
        records = []
        for key in registos:
            for year in registos[key]:
                for month in registos[key][year]:
                    for day in registos[key][year][month]:
                        for hour in registos[key][year][month][day]:
                            records.append(registos[key][year][month][day][hour])
    else:
        records = [message['data']]
        print(records)

    # convert to dataframe
    df = pd.DataFrame(records)

    df = df.mask(df.isnull(), 0)

    df['tempo_ecra'] = df['tempo_ecra'].apply(lambda x: x / 120.0)

    return df

def make_path(key, year, month, day, hour):
    time = hour.split(':')
    print(time)
    def len_two(s):
        return ('0' if len(s) == 1 else '') + s
    norm_hour = len_two(time[0])
    norm_min  = len_two(time[1])
    norm_sec  = len_two(time[2])
    path = '/{}/{}/{}/{}/{}:{}:{}'.format(key, year, month, day, norm_hour, norm_min, norm_sec)
    print(path)
    return path

def task():
    firebase = pyrebase.initialize_app(config)

    db = firebase.database()

    nbatch = 5
    trained = False
    model = MiniBatchKMeans(
        n_clusters=2,
        batch_size=nbatch
    )
    to_train = pd.DataFrame(None)

    def training_handler(message):
        nonlocal model
        nonlocal trained
        nonlocal to_train

        df = build_dataframe(message)
        pprint.pprint(df)

        to_train = to_train.append(df)

        while to_train.shape[0] >= nbatch: # pelo menos n_clusters registos
            model = model.fit(to_train[0:nbatch])
            to_train = to_train[nbatch:]
            trained = True

        if trained:
            print(model.cluster_centers_)

    to_classify = []
    def query_handler(message):
        nonlocal model
        nonlocal to_classify
        nonlocal trained
        nonlocal db

        if message['path'] == '/' and message['data'] != None: # chunk inicial
            registos = message['data']
            # build list of dictionaries
            records = []
            for key in registos:
                for year in registos[key]:
                    for month in registos[key][year]:
                        for day in registos[key][year][month]:
                            for hour in registos[key][year][month][day]:
                                to_classify.append((make_path(key, year, month, day, hour), registos[key][year][month][day][hour]))
        else:
            to_classify.append((message['path'], message['data']))

        if trained:
            for path, record in to_classify:
                if record != None:
                    df = pd.DataFrame(record, index=[0])
                    classification = model.predict(df)
                    data = {path:{"classificacao":int(classification[0])}}
                    pprint.pprint(data)
                    db.child("resultados_input" + ('/' if path[0] != '/' else '') + path).remove()
                    db.child("classificacoes").update(data)
            to_classify = []

    training_stream = db.child("registos").stream(training_handler)
    query_stream = db.child("resultados_input").stream(query_handler)

task()
