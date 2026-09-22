export const titles = {
  tickets: 'Билеты',
  coordinates: 'Координаты',
  locations: 'Локации',
  persons: 'Люди',
  events: 'События',
  venues: 'Площадки',
};
export const labels = {
  VIP: 'VIP',
  USUAL: 'Обычный',
  CHEAP: 'Бюджетный',
  GREEN: 'Зелёный',
  BLUE: 'Синий',
  YELLOW: 'Жёлтый',
  WHITE: 'Белый',
  CONCERT: 'Концерт',
  BASKETBALL: 'Баскетбол',
  OPERA: 'Опера',
  THEATRE_PERFORMANCE: 'Спектакль',
  PUB: 'Паб',
  LOFT: 'Лофт',
  THEATRE: 'Театр',
};
const text = (key, label, extra = {}) => ({ key, label, kind: 'text', required: true, ...extra });
const num = (key, label, extra = {}) => ({
  key,
  label,
  kind: 'number',
  required: true,
  min: 1,
  max: '9223372036854775807',
  ...extra,
});
const select = (key, label, values, required = true) => ({
  key,
  label,
  kind: 'select',
  values,
  required,
});
const ref = (key, label, source, required = true) => ({
  key,
  label,
  kind: 'ref',
  source,
  required,
});
const colors = ['GREEN', 'BLUE', 'YELLOW', 'WHITE'];
export const schemas = {
  tickets: [
    text('name', 'Название билета'),
    ref('coordinatesId', 'Координаты', 'coordinates'),
    ref('eventId', 'Событие', 'events'),
    ref('venueId', 'Площадка', 'venues'),
    num('price', 'Цена, ₽', { max: 2147483647 }),
    num('discount', 'Скидка, %', { max: 100 }),
    select('type', 'Тип билета', ['VIP', 'USUAL', 'CHEAP'], false),
    num('number', 'Номер', { required: false }),
    ref('personId', 'Человек', 'persons', false),
  ],
  coordinates: [
    num('x', 'X', { min: undefined, max: 156, step: 'any' }),
    num('y', 'Y', { min: -2147483648, max: 2147483647 }),
  ],
  locations: [
    text('name', 'Название локации', { allowEmpty: true }),
    num('x', 'X', { min: undefined, max: undefined, step: 'any' }),
    num('y', 'Y', { min: -2147483648, max: 2147483647 }),
    num('z', 'Z', { min: undefined, max: undefined, step: 'any' }),
  ],
  persons: [
    select('eyeColor', 'Цвет глаз', colors, false),
    select('hairColor', 'Цвет волос', colors, false),
    ref('locationId', 'Локация', 'locations'),
    num('weight', 'Вес, кг', { required: false }),
  ],
  events: [
    text('name', 'Название события'),
    num('ticketsCount', 'Количество билетов'),
    select('eventType', 'Тип события', ['CONCERT', 'BASKETBALL', 'OPERA', 'THEATRE_PERFORMANCE']),
  ],
  venues: [
    text('name', 'Название площадки'),
    num('capacity', 'Вместимость', { required: false }),
    select('type', 'Тип площадки', ['PUB', 'LOFT', 'THEATRE']),
  ],
};
export function refLabel(kind, item) {
  if (kind === 'coordinates') return `#${item.id} · (${item.x}; ${item.y})`;
  if (kind === 'persons')
    return `#${item.id} · ${item.location.name || 'Без названия'}${item.weight ? ` · ${item.weight} кг` : ''}`;
  return `#${item.id} · ${item.name || 'Без названия'}`;
}
export function initialValues(kind, item) {
  const values = {};
  for (const field of schemas[kind]) {
    let value = item?.[field.key];
    if (field.kind === 'ref') {
      const property = field.key.replace(/Id$/, '');
      value = item?.[property]?.id;
    }
    values[field.key] = value ?? '';
  }
  return values;
}

export function requestValues(kind, values) {
  const request = {};
  for (const field of schemas[kind]) {
    const value = values[field.key];
    request[field.key] = value === '' && !field.required ? null : value;
  }
  return request;
}

const ticketIdField = {
  key: 'ticketId',
  label: 'ID билета',
  kind: 'number',
  required: true,
  min: 1,
};

export const operationFields = {
  count: [ref('venueId', 'Площадка', 'venues')],
  less: [ref('venueId', 'Площадка', 'venues')],
  prefix: [text('prefix', 'Начало названия (с учётом регистра)', { allowEmpty: true })],
  sell: [
    ticketIdField,
    ref('personId', 'Человек', 'persons'),
    num('amount', 'Сумма продажи, ₽', { max: 2147483647 }),
  ],
  clone: [ticketIdField, num('discount', 'Скидка, %', { max: 100 })],
};
