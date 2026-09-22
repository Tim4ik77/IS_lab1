import { labels } from '../schema';

const detailLabels = {
  id: 'ID',
  name: 'Название',
  coordinates: 'Координаты',
  creationDate: 'Дата создания',
  person: 'Человек',
  event: 'Событие',
  price: 'Цена, ₽',
  type: 'Тип',
  discount: 'Скидка, %',
  number: 'Номер',
  venue: 'Площадка',
  eyeColor: 'Цвет глаз',
  hairColor: 'Цвет волос',
  location: 'Локация',
  weight: 'Вес, кг',
  ticketsCount: 'Количество билетов',
  eventType: 'Тип события',
  capacity: 'Вместимость',
  x: 'X',
  y: 'Y',
  z: 'Z',
};
function displayValue(key, value) {
  if (value === null) {
    return 'Не указано';
  }
  if (key === 'creationDate') {
    return new Date(value).toLocaleString('ru-RU');
  }
  if (['type', 'eventType', 'eyeColor', 'hairColor'].includes(key)) {
    return labels[value];
  }
  return String(value);
}

export default function Details({ value }) {
  return (
    <dl className="details">
      {Object.entries(value).map(([key, fieldValue]) => {
        const nested = fieldValue !== null && typeof fieldValue === 'object';
        return (
          <div className={nested ? 'detail-group' : 'detail-row'} key={key}>
            <dt>{detailLabels[key] || key}</dt>
            <dd>{nested ? <Details value={fieldValue} /> : displayValue(key, fieldValue)}</dd>
          </div>
        );
      })}
    </dl>
  );
}
