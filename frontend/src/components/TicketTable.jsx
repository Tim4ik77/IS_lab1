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
            ].map((heading) => (
              <th key={heading}>{heading}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {items.map((ticket) => (
            <tr key={ticket.id}>
              <td>#{ticket.id}</td>
              <td>
                <a href={`#/tickets/${ticket.id}`}>{ticket.name}</a>
              </td>
              <td>
                {ticket.coordinates.x}; {ticket.coordinates.y}
              </td>
              <td>{new Date(ticket.creationDate).toLocaleString('ru-RU')}</td>
              <td>
                {ticket.person ? (
                  <>
                    <span>#{ticket.person.id}</span>
                    <small className="cell-sub">{ticket.person.location.name || 'Без названия'}</small>
                  </>
                ) : (
                  '—'
                )}
              </td>
              <td>{ticket.event.name}</td>
              <td>{ticket.price.toLocaleString('ru-RU')} ₽</td>
              <td>{ticket.type ? <span>{labels[ticket.type]}</span> : '—'}</td>
              <td>{ticket.discount}%</td>
              <td>{ticket.number ?? '—'}</td>
              <td>{ticket.venue.name}</td>
              <td>
                <div className="actions">
                  <button onClick={() => onEdit(ticket)}>Изменить</button>
                  <button onClick={() => onDelete(ticket)}>Удалить</button>
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
