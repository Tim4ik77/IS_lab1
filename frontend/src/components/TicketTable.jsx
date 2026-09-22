import { labels } from '../schema';

export default function TicketTable({ items, onEdit, onDelete }) {
  return (
    <div className="table-scroll">
      <table className="ticket-table">
        <thead>
          <tr>
            {[
              'ID',
              'Название',
              'Координаты',
              'Создан',
              'Человек / локация',
              'Событие',
              'Цена',
              'Тип',
              'Скидка',
              'Номер',
              'Площадка',
              'Действия',
            ].map((h) => (
              <th key={h}>{h}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {items.map((t) => (
            <tr key={t.id}>
              <td>#{t.id}</td>
              <td>
                <a href={`#/tickets/${t.id}`}>{t.name}</a>
              </td>
              <td>
                {t.coordinates.x}; {t.coordinates.y}
              </td>
              <td>{new Date(t.creationDate).toLocaleString('ru-RU')}</td>
              <td>
                {t.person ? (
                  <>
                    <span>#{t.person.id}</span>
                    <small className="cell-sub">{t.person.location.name || 'Без названия'}</small>
                  </>
                ) : (
                  '—'
                )}
              </td>
              <td>{t.event.name}</td>
              <td>{t.price.toLocaleString('ru-RU')} ₽</td>
              <td>{t.type ? <span>{labels[t.type]}</span> : '—'}</td>
              <td>{t.discount}%</td>
              <td>{t.number ?? '—'}</td>
              <td>{t.venue.name}</td>
              <td>
                <div className="actions">
                  <button onClick={() => onEdit(t)}>Изменить</button>
                  <button onClick={() => onDelete(t)}>Удалить</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {items.length === 0 && (
        <div className="empty">
          <h3>Билетов пока нет</h3>
          <p>Создайте билет или измените условия поиска.</p>
        </div>
      )}
    </div>
  );
}
