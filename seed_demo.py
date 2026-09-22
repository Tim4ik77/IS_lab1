"""Optional sample data. Run against the running local server: python scripts/seed_demo.py."""
import json
import urllib.request

BASE = 'http://127.0.0.1:8080/api'

def call(path, data=None):
    request = urllib.request.Request(BASE + path, data=None if data is None else json.dumps(data).encode('utf-8'), headers={'Content-Type': 'application/json'})
    with urllib.request.urlopen(request, timeout=10) as response:
        return json.load(response)

def reference(kind, data):
    return call('/references/' + kind, data)['id']

if __name__ == '__main__':
    if int(call('/tickets')['total']) > 0:
        raise SystemExit('Skipped: the ticket collection is not empty.')
    coordinates = [reference('coordinates', {'x':x,'y':y}) for x,y in [(42.5,18),(120,35),(-12.5,64)]]
    locations = [reference('locations', {'name':name,'x':x,'y':y,'z':0}) for name,x,y in [('Санкт-Петербург',30.3,60),('Москва',37.6,56)]]
    people = [reference('persons', {'locationId':locations[i], 'weight':70+i*10, 'eyeColor':['GREEN','BLUE'][i], 'hairColor':['WHITE','YELLOW'][i]}) for i in range(2)]
    venues = [reference('venues', {'name':name,'capacity':capacity,'type':kind}) for name,capacity,kind in [('Мариинский театр',1800,'THEATRE'),('Севкабель Порт',3000,'LOFT'),('Джаз-клуб',120,'PUB')]]
    events = [reference('events', {'name':name,'ticketsCount':count,'eventType':kind}) for name,count,kind in [('Вечер джаза',120,'CONCERT'),('Евгений Онегин',900,'OPERA'),('Чайка',600,'THEATRE_PERFORMANCE'),('Большой концерт',2000,'CONCERT')]]
    names = ['Джаз у воды','Вечер в опере','Театральный вечер','Музыка города','Джаз: первый ряд','Опера: партер','Чайка: балкон','Концерт: танцпол','Джаз: у сцены','Опера: ложа','Спектакль: партер','Концерт: VIP']
    for i,name in enumerate(names):
        call('/tickets', {'name':name,'coordinatesId':coordinates[i%3], 'eventId':events[i%4], 'venueId':venues[[2,0,0,1][i%4]], 'personId':people[i%2] if i%3==0 else None, 'price':1200+i*350, 'type':['USUAL','VIP','CHEAP'][i%3], 'discount':[5,10,15][i%3], 'number':i+1})
    print('Created 12 demo tickets and their reference objects.')
