import { useState } from 'react';
import { api } from '../api';
import useRefresh from '../hooks/useRefresh';
import ErrorMessage from '../components/ErrorMessage';
import Details from '../components/Details';

export default function TicketDetails({ id, revision, onEdit, onDelete }) {
  const [ticket, setTicket] = useState(null);
  const [error, setError] = useState(null);
  useRefresh(
    async (active) => {
      try {
        const result = await api(`/tickets/${id}`);
        if (active()) {
          setTicket(result);
          setError(null);
        }
      } catch (e) {
        if (active()) {
          setError(e);
          setTicket(null);
        }
      }
    },
    [id, revision],
  );
  return (
    <>
      <a href="#/tickets">← Все билеты</a>
      <div className="toolbar">
        <div>
          <h1>{ticket?.name || `Билет #${id}`}</h1>
        </div>
        {ticket && (
          <div className="actions">
            <button onClick={() => onDelete(ticket)}>Удалить</button>
            <button onClick={() => onEdit(ticket)}>Изменить билет</button>
          </div>
        )}
      </div>
      <ErrorMessage error={error} />
      {ticket ? (
        <div>
          <Details value={ticket} />
        </div>
      ) : (
        !error && <p>Загрузка…</p>
      )}
    </>
  );
}
