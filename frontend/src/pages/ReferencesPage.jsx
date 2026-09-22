import { useState } from 'react';
import { referenceKinds } from '../api';
import { schemas, titles, refLabel, labels } from '../schema';
import Details from '../components/Details';
import Modal from '../components/Modal';

function displayField(item, field) {
  if (field.kind === 'ref') {
    const property = field.key.replace(/Id$/, '');
    return refLabel(field.source, item[property]);
  }
  const value = item[field.key];
  if (value == null) {
    return '—';
  }
  return field.kind === 'select' ? labels[value] : String(value);
}

export default function ReferencesPage({ references, onCreate, onEdit, onDelete }) {
  const [kind, setKind] = useState('events');
  const [page, setPage] = useState(0);
  const [view, setView] = useState(null);
  const rows = references[kind] || [];
  const visiblePage = Math.min(page, Math.max(0, Math.ceil(rows.length / 10) - 1));
  const viewedItem = rows.find((item) => item.id === view?.id);

  return (
    <>
      <div className="toolbar">
        <div>
          <h1>Связанные объекты</h1>
        </div>
        <button onClick={() => onCreate(kind)}>+ Создать объект</button>
      </div>
      <div className="toolbar" aria-label="Справочники">
        {referenceKinds.map((referenceKind) => (
          <button
            aria-pressed={kind === referenceKind}
            key={referenceKind}
            onClick={() => {
              setKind(referenceKind);
              setPage(0);
            }}
          >
            {titles[referenceKind]} <span>{references[referenceKind]?.length || 0}</span>
          </button>
        ))}
      </div>
      <div>
        <div className="toolbar">
          <h2>{titles[kind]}</h2>
          <span>{rows.length} объектов</span>
        </div>
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                {schemas[kind].map((field) => (
                  <th key={field.key}>{field.label}</th>
                ))}
                <th>Действия</th>
              </tr>
            </thead>
            <tbody>
              {rows.slice(visiblePage * 10, (visiblePage + 1) * 10).map((item) => (
                <tr key={item.id}>
                  <td>#{item.id}</td>
                  {schemas[kind].map((field) => (
                    <td key={field.key}>{displayField(item, field)}</td>
                  ))}
                  <td>
                    <div className="actions">
                      <button onClick={() => setView(item)}>Просмотр</button>
                      <button onClick={() => onEdit(kind, item)}>Изменить</button>
                      <button onClick={() => onDelete(kind, item)}>Удалить</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {!rows.length && (
            <div>
              <h3>Здесь пока пусто</h3>
              <p>Добавьте первый объект, чтобы использовать его в билетах.</p>
              <button onClick={() => onCreate(kind)}>Создать объект</button>
            </div>
          )}
        </div>
        <div className="pagination">
          <span>
            Страница {visiblePage + 1} из {Math.max(1, Math.ceil(rows.length / 10))}
          </span>
          <div>
            <button disabled={!visiblePage} onClick={() => setPage(visiblePage - 1)}>
              ←
            </button>
            <button
              disabled={(visiblePage + 1) * 10 >= rows.length}
              onClick={() => setPage(visiblePage + 1)}
            >
              →
            </button>
          </div>
        </div>
      </div>
      <p className="hint">
        Для человека сначала создайте локацию. Для билета нужны координаты, событие и площадка.
      </p>
      {view && (
        <Modal title={`${titles[kind]} · #${view.id}`} onClose={() => setView(null)}>
          {viewedItem ? (
            <>
              <Details value={viewedItem} />
              <div className="actions">
                <button
                  onClick={() => {
                    onEdit(kind, viewedItem);
                    setView(null);
                  }}
                >
                  Изменить
                </button>
              </div>
            </>
          ) : (
            <p>Объект удалён другим клиентом.</p>
          )}
        </Modal>
      )}
    </>
  );
}
