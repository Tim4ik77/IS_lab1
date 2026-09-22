import { useEffect, useState } from 'react';
import { getReferences, referenceKinds } from './api';
import useRefresh from './hooks/useRefresh';
import ErrorMessage from './components/ErrorMessage';
import EditDialog from './components/EditDialog';
import DeleteDialog from './components/DeleteDialog';
import TicketsPage from './pages/TicketsPage';
import TicketDetails from './pages/TicketDetails';
import ReferencesPage from './pages/ReferencesPage';
import OperationsPage from './pages/OperationsPage';

export default function App() {
  const [route, setRoute] = useState(location.hash.slice(1) || '/tickets');
  const [references, setReferences] = useState(
    Object.fromEntries(referenceKinds.map((kind) => [kind, []])),
  );
  const [error, setError] = useState(null);
  const [dialog, setDialog] = useState(null);
  const [revision, setRevision] = useState(0);

  useEffect(() => {
    function changeRoute() {
      setRoute(location.hash.slice(1) || '/tickets');
    }
    window.addEventListener('hashchange', changeRoute);
    return () => window.removeEventListener('hashchange', changeRoute);
  }, []);

  useRefresh(
    async (isActive) => {
      try {
        const data = await getReferences();
        if (isActive()) {
          setReferences(data);
          setError(null);
        }
      } catch (error) {
        if (isActive()) setError(error);
      }
    },
    [revision],
  );

  function edit(kind, item = null) {
    setDialog({ action: 'edit', kind, item });
  }
  function remove(kind, item) {
    setDialog({ action: 'delete', kind, item });
  }
  function refresh() {
    setRevision((value) => value + 1);
  }
  function saved() {
    setDialog(null);
    refresh();
  }
  function deleted() {
    if (dialog.kind === 'tickets' && route === `/tickets/${dialog.item.id}`) {
      location.hash = '/tickets';
    }
    saved();
  }

  const detailId = route.match(/^\/tickets\/(\d+)$/)?.[1];
  let page;
  if (detailId) {
    page = (
      <TicketDetails
        key={detailId}
        id={detailId}
        revision={revision}
        onEdit={(item) => edit('tickets', item)}
        onDelete={(item) => remove('tickets', item)}
      />
    );
  } else if (route === '/references') {
    page = (
      <ReferencesPage
        references={references}
        onCreate={(kind) => edit(kind)}
        onEdit={edit}
        onDelete={remove}
      />
    );
  } else if (route === '/operations') {
    page = (
      <OperationsPage
        references={references}
        revision={revision}
        onChanged={refresh}
        onEdit={(item) => edit('tickets', item)}
        onDelete={(item) => remove('tickets', item)}
      />
    );
  } else {
    page = (
      <TicketsPage
        revision={revision}
        onCreate={() => edit('tickets')}
        onEdit={(item) => edit('tickets', item)}
        onDelete={(item) => remove('tickets', item)}
      />
    );
  }

  return (
    <div className="app">
      <nav className="menu" aria-label="Главное меню">
        <a href="#/tickets" aria-current={route.startsWith('/tickets') ? 'page' : undefined}>
          Билеты
        </a>
        <a href="#/references" aria-current={route === '/references' ? 'page' : undefined}>
          Связанные объекты
        </a>
        <a href="#/operations" aria-current={route === '/operations' ? 'page' : undefined}>
          Специальные операции
        </a>
      </nav>
      <main>
        <ErrorMessage error={error} />
        {page}
      </main>
      {dialog?.action === 'edit' && (
        <EditDialog
          {...dialog}
          references={references}
          onClose={() => setDialog(null)}
          onSaved={saved}
        />
      )}
      {dialog?.action === 'delete' && (
        <DeleteDialog
          {...dialog}
          references={references}
          onClose={() => setDialog(null)}
          onDeleted={deleted}
        />
      )}
    </div>
  );
}
