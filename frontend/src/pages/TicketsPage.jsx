import { useState } from 'react';
import useTickets from '../hooks/useTickets';
import ErrorMessage from '../components/ErrorMessage';
import TicketTable from '../components/TicketTable';
import TicketFilters from '../components/TicketFilters';
import TicketSortControls from '../components/TicketSortControls';
import Pagination from '../components/Pagination';

export default function TicketsPage({ revision, onCreate, onEdit, onDelete }) {
  const tickets = useTickets(revision);
  const [ticketId, setTicketId] = useState('');

  function openTicket(event) {
    event.preventDefault();
    location.hash = `/tickets/${ticketId}`;
  }

  return (
    <>
      <div className="toolbar">
        <div><h1>Билеты <span>{tickets.data.total}</span></h1></div>
        <button onClick={onCreate}>Создать билет</button>
      </div>
      <div>
        <TicketFilters filters={tickets.filters} onApply={tickets.applyFilter}
          onRemove={tickets.removeFilter} onClear={tickets.clearFilters}>
          <TicketSortControls sortField={tickets.sortField} sortDirection={tickets.sortDirection}
            onSortChange={tickets.changeSort} onDirectionChange={tickets.changeDirection} />
        </TicketFilters>
        <ErrorMessage error={tickets.error} />
        {tickets.loading ? (
          <div>Загружаем билеты…</div>
        ) : (
          <TicketTable items={tickets.data.items} onEdit={onEdit} onDelete={onDelete} />
        )}
        <Pagination page={tickets.page} pageSize={tickets.pageSize} total={tickets.data.total}
          onPageChange={tickets.setPage} onPageSizeChange={tickets.changePageSize} />
      </div>
      <form className="toolbar" onSubmit={openTicket}>
        <label htmlFor="lookup-id">Открыть билет по ID</label>
        <input id="lookup-id" type="number" min="1" step="1" required value={ticketId}
          onChange={(event) => setTicketId(event.target.value)} placeholder="Например, 1" />
        <button>Открыть →</button>
      </form>
    </>
  );
}
