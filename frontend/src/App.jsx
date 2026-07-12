import React, { useState, useEffect } from 'react';
import { 
  LayoutDashboard, Bed, CalendarDays, Utensils, ChefHat, 
  Receipt, Warehouse, Database, Plus, Search, Check, 
  X, AlertTriangle, TrendingUp, Clock, Sparkles, Trash2, Edit 
} from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [rooms, setRooms] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [menuItems, setMenuItems] = useState([]);
  const [orders, setOrders] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  // Db status checker
  const [dbConnected, setDbConnected] = useState(true);

  // Search & Filter States
  const [roomFilter, setRoomFilter] = useState('ALL');
  const [menuFilter, setMenuFilter] = useState('ALL');
  
  // Modals States
  const [showRoomModal, setShowRoomModal] = useState(false);
  const [showReservationModal, setShowReservationModal] = useState(false);
  const [showMenuModal, setShowMenuModal] = useState(false);
  const [showOrderModal, setShowOrderModal] = useState(false);
  const [showInventoryModal, setShowInventoryModal] = useState(false);
  const [showInvoiceModal, setShowInvoiceModal] = useState(false);

  // Selected Items / Forms state
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [newRoom, setNewRoom] = useState({ roomNumber: '', roomType: 'STANDARD', pricePerNight: '', bedCount: 1, amenities: '', status: 'AVAILABLE' });
  const [newReservation, setNewReservation] = useState({ customerName: '', roomNumber: '', checkInDate: '', checkOutDate: '' });
  const [newMenuItem, setNewMenuItem] = useState({ name: '', description: '', price: '', category: 'STARTER', available: true });
  const [newOrder, setNewOrder] = useState({ roomNumber: '', itemIds: [] });
  const [inventoryUpdate, setInventoryUpdate] = useState({ itemId: '', quantityChange: '' });
  const [seedParams, setSeedParams] = useState({ customers: 100, reservations: 150, orders: 200, items: 400 });

  // Load baseline data on launch
  useEffect(() => {
    fetchAllData();
  }, []);

  const fetchAllData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [roomsRes, resRes, menuRes, ordersRes, invRes] = await Promise.all([
        fetch('/api/rooms').then(res => res.ok ? res.json() : []),
        fetch('/api/reservations').then(res => res.ok ? res.json() : []),
        fetch('/api/menu').then(res => res.ok ? res.json() : []),
        fetch('/api/orders').then(res => res.ok ? res.json() : []),
        fetch('/api/inventory').then(res => res.ok ? res.json() : [])
      ]);
      setRooms(roomsRes);
      setReservations(resRes);
      setMenuItems(menuRes);
      setOrders(ordersRes);
      setInventory(invRes);
      setDbConnected(true);
    } catch (err) {
      console.error("Failed to load data", err);
      setDbConnected(false);
      setError("Unable to connect to the backend server. Make sure it's running on port 8080.");
    } finally {
      setLoading(false);
    }
  };

  // Setup Database Baseline Data
  const handleSetupDatabase = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch('/api/generator/setup', { method: 'POST' });
      if (res.ok) {
        alert("Baseline catalog data (rooms, menu, and inventory) successfully loaded!");
        fetchAllData();
      } else {
        setError("Error setting up data. Check backend logs.");
      }
    } catch (err) {
      setError("Network error triggering generator.");
    } finally {
      setLoading(false);
    }
  };

  // Seed Millions scaling data
  const handleSeedMillions = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(`/api/generator/millions?customersCount=${seedParams.customers}&reservationsCount=${seedParams.reservations}&ordersCount=${seedParams.orders}&orderItemsCount=${seedParams.items}`, {
        method: 'POST'
      });
      if (res.ok) {
        const text = await res.text();
        alert(text);
        fetchAllData();
      } else {
        setError("Scaling seed request failed.");
      }
    } catch (err) {
      setError("Network error triggering millions generator.");
    } finally {
      setLoading(false);
    }
  };

  // Create Room
  const handleCreateRoom = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('/api/rooms', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...newRoom,
          pricePerNight: parseFloat(newRoom.pricePerNight)
        })
      });
      if (res.ok) {
        setShowRoomModal(false);
        setNewRoom({ roomNumber: '', roomType: 'STANDARD', pricePerNight: '', bedCount: 1, amenities: '', status: 'AVAILABLE' });
        fetchAllData();
      } else {
        const msg = await res.text();
        alert("Error creating room: " + msg);
      }
    } catch (err) {
      alert("Failed to submit room.");
    }
  };

  // Create Reservation
  const handleCreateReservation = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('/api/reservations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newReservation)
      });
      if (res.ok) {
        setShowReservationModal(false);
        setNewReservation({ customerName: '', roomNumber: '', checkInDate: '', checkOutDate: '' });
        fetchAllData();
      } else {
        const data = await res.json();
        alert("Booking failed: " + (data.message || "Room may not be available for the selected dates."));
      }
    } catch (err) {
      alert("Failed to make reservation.");
    }
  };

  // Cancel Booking
  const handleCancelBooking = async (id) => {
    if (!confirm("Are you sure you want to cancel this booking?")) return;
    try {
      const res = await fetch(`/api/reservations/${id}/cancel`, { method: 'PUT' });
      if (res.ok) {
        fetchAllData();
      } else {
        alert("Unable to cancel booking.");
      }
    } catch (err) {
      alert("Error cancelling booking.");
    }
  };

  // Complete Booking (Check-out)
  const handleCompleteBooking = async (id) => {
    try {
      const res = await fetch(`/api/reservations/${id}/complete`, { method: 'PUT' });
      if (res.ok) {
        // Automatically trigger invoice load
        handleViewInvoice(id);
      } else {
        alert("Unable to complete checkout.");
      }
    } catch (err) {
      alert("Error checking out guest.");
    }
  };

  // View Invoice
  const handleViewInvoice = async (reservationId) => {
    try {
      const res = await fetch(`/api/billing/reservation/${reservationId}`);
      if (res.ok) {
        const data = await res.json();
        setSelectedInvoice(data);
        setShowInvoiceModal(true);
        fetchAllData(); // Refresh list to get completed status
      } else {
        alert("No billing details found for this reservation.");
      }
    } catch (err) {
      alert("Failed to retrieve invoice.");
    }
  };

  // Create Menu Item
  const handleCreateMenuItem = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('/api/menu', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...newMenuItem,
          price: parseFloat(newMenuItem.price)
        })
      });
      if (res.ok) {
        setShowMenuModal(false);
        setNewMenuItem({ name: '', description: '', price: '', category: 'STARTER', available: true });
        fetchAllData();
      } else {
        alert("Failed to create menu item.");
      }
    } catch (err) {
      alert("Error creating menu item.");
    }
  };

  // Create Kitchen / Room Order
  const handleCreateOrder = async (e) => {
    e.preventDefault();
    if (newOrder.itemIds.length === 0) {
      alert("Please select at least one item from the menu.");
      return;
    }
    
    // Group selected items to form MenuItemQuantities list
    const itemsMap = {};
    newOrder.itemIds.forEach(id => {
      itemsMap[id] = (itemsMap[id] || 0) + 1;
    });

    const itemsPayload = Object.keys(itemsMap).map(id => ({
      menuItemId: parseInt(id),
      quantity: itemsMap[id]
    }));

    try {
      const res = await fetch('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          roomNumber: newOrder.roomNumber,
          items: itemsPayload
        })
      });
      if (res.ok) {
        setShowOrderModal(false);
        setNewOrder({ roomNumber: '', itemIds: [] });
        fetchAllData();
      } else {
        alert("Failed to place order. Check if room is active/occupied.");
      }
    } catch (err) {
      alert("Error placing order.");
    }
  };

  // Update Order Status
  const handleUpdateOrderStatus = async (id, status) => {
    try {
      const res = await fetch(`/api/orders/${id}/status?status=${status}`, { method: 'PUT' });
      if (res.ok) {
        fetchAllData();
      } else {
        alert("Failed to update status.");
      }
    } catch (err) {
      alert("Error changing status.");
    }
  };

  // Update Inventory Stock
  const handleUpdateInventory = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`/api/inventory/${inventoryUpdate.itemId}/stock?quantityChange=${parseFloat(inventoryUpdate.quantityChange)}`, {
        method: 'PUT'
      });
      if (res.ok) {
        setShowInventoryModal(false);
        setInventoryUpdate({ itemId: '', quantityChange: '' });
        fetchAllData();
      } else {
        alert("Failed to update stock quantity.");
      }
    } catch (err) {
      alert("Error updating inventory.");
    }
  };

  // Render Functions
  const renderDashboard = () => {
    const occupiedRooms = rooms.filter(r => r.status === 'OCCUPIED').length;
    const activeBookings = reservations.filter(r => r.status === 'CONFIRMED').length;
    const pendingOrders = orders.filter(o => o.status === 'PENDING' || o.status === 'PREPARING').length;
    const lowStockAlerts = inventory.filter(i => i.quantity <= i.minimumStockLevel).length;

    return (
      <div className="dashboard-view animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
        <div className="grid-stats">
          <div className="glass-card stat-card">
            <div className="stat-info">
              <h3>Occupancy Rate</h3>
              <p>{rooms.length ? Math.round((occupiedRooms / rooms.length) * 100) : 0}%</p>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{occupiedRooms} of {rooms.length} Rooms</span>
            </div>
            <div className="stat-icon" style={{ background: 'rgba(99, 102, 241, 0.15)', color: 'var(--accent)' }}>
              <Bed size={24} />
            </div>
          </div>

          <div className="glass-card stat-card">
            <div className="stat-info">
              <h3>Active Bookings</h3>
              <p>{activeBookings}</p>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Check-ins today</span>
            </div>
            <div className="stat-icon" style={{ background: 'rgba(59, 130, 246, 0.15)', color: 'var(--info)' }}>
              <CalendarDays size={24} />
            </div>
          </div>

          <div className="glass-card stat-card">
            <div className="stat-info">
              <h3>Active Orders</h3>
              <p>{pendingOrders}</p>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>In preparation</span>
            </div>
            <div className="stat-icon" style={{ background: 'rgba(16, 185, 129, 0.15)', color: 'var(--success)' }}>
              <ChefHat size={24} />
            </div>
          </div>

          <div className="glass-card stat-card">
            <div className="stat-info">
              <h3>Stock Alerts</h3>
              <p style={{ color: lowStockAlerts > 0 ? 'var(--error)' : 'var(--text-primary)' }}>{lowStockAlerts}</p>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Items below min level</span>
            </div>
            <div className="stat-icon" style={{ background: 'rgba(239, 68, 68, 0.15)', color: 'var(--error)' }}>
              <AlertTriangle size={24} />
            </div>
          </div>
        </div>

        {/* Quick Operations panel */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))', gap: '24px' }}>
          <div className="glass-card">
            <h3 style={{ marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Sparkles size={20} color="var(--accent)" /> Quick Actions
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <button className="btn btn-secondary" onClick={() => { setNewReservation({ ...newReservation, checkInDate: new Date().toISOString().split('T')[0] }); setShowReservationModal(true); }}>
                <CalendarDays size={16} /> Book a Room
              </button>
              <button className="btn btn-secondary" onClick={() => setShowOrderModal(true)}>
                <Utensils size={16} /> Place Order
              </button>
              <button className="btn btn-secondary" onClick={() => setShowRoomModal(true)}>
                <Plus size={16} /> Add New Room
              </button>
              <button className="btn btn-secondary" onClick={() => setShowMenuModal(true)}>
                <Plus size={16} /> Add Menu Item
              </button>
            </div>
          </div>

          <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <h3><Database size={20} color="var(--accent)" /> Database Setup Tools</h3>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: '1.4' }}>
              Reset databases and seed mock catalog records or trigger large-scale test generators to evaluate system performance.
            </p>
            <div style={{ display: 'flex', gap: '12px' }}>
              <button className="btn btn-primary" onClick={handleSetupDatabase}>Seed Catalog Data</button>
              <button className="btn btn-secondary" onClick={() => setActiveTab('generator')}>Custom Scaling Generator</button>
            </div>
          </div>
        </div>

        {/* Live Active Reservations / Kitchen orders list */}
        <div className="glass-card">
          <h3 style={{ marginBottom: '16px' }}>Pending Kitchen / Room Service Orders</h3>
          <div className="table-container">
            {orders.filter(o => o.status !== 'DELIVERED').length === 0 ? (
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', padding: '10px 0' }}>No active orders in preparation.</p>
            ) : (
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Room #</th>
                    <th>Ordered Items</th>
                    <th>Total Price</th>
                    <th>Order Time</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.filter(o => o.status !== 'DELIVERED').map(order => (
                    <tr key={order.id}>
                      <td><span className="badge badge-info">{order.roomNumber}</span></td>
                      <td>
                        {order.items.map(it => (
                          <div key={it.id} style={{ fontSize: '0.85rem' }}>
                            {it.menuItemName} <span style={{ color: 'var(--text-secondary)' }}>x{it.quantity}</span>
                          </div>
                        ))}
                      </td>
                      <td>${order.totalPrice.toFixed(2)}</td>
                      <td>{new Date(order.orderTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</td>
                      <td>
                        <span className={`badge ${order.status === 'PENDING' ? 'badge-warning' : 'badge-info'}`}>
                          {order.status}
                        </span>
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '8px' }}>
                          {order.status === 'PENDING' && (
                            <button className="btn btn-secondary" style={{ padding: '6px 10px', fontSize: '0.8rem' }} onClick={() => handleUpdateOrderStatus(order.id, 'PREPARING')}>
                              Start Cook
                            </button>
                          )}
                          {order.status === 'PREPARING' && (
                            <button className="btn btn-primary" style={{ padding: '6px 10px', fontSize: '0.8rem' }} onClick={() => handleUpdateOrderStatus(order.id, 'DELIVERED')}>
                              Deliver Order
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    );
  };

  const renderRooms = () => {
    return (
      <div className="rooms-view animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', gap: '8px' }}>
            {['ALL', 'AVAILABLE', 'OCCUPIED', 'MAINTENANCE'].map(opt => (
              <button 
                key={opt}
                className={`tab-btn ${roomFilter === opt ? 'active' : ''}`}
                onClick={() => setRoomFilter(opt)}
              >
                {opt}
              </button>
            ))}
          </div>
          <button className="btn btn-primary" onClick={() => setShowRoomModal(true)}>
            <Plus size={16} /> Add Room
          </button>
        </div>

        <div className="grid-stats" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))' }}>
          {rooms
            .filter(r => roomFilter === 'ALL' || r.status === roomFilter)
            .map(room => (
              <div className="glass-card" key={room.id} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div>
                    <h3 style={{ fontFamily: 'var(--font-display)', fontSize: '1.25rem' }}>Room {room.roomNumber}</h3>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{room.roomType}</span>
                  </div>
                  <span className={`badge ${
                    room.status === 'AVAILABLE' ? 'badge-success' : 
                    room.status === 'OCCUPIED' ? 'badge-info' : 'badge-error'
                  }`}>
                    {room.status}
                  </span>
                </div>
                
                <div style={{ padding: '8px 0', borderTop: '1px solid rgba(255,255,255,0.05)', borderBottom: '1px solid rgba(255,255,255,0.05)', fontSize: '0.9rem', display: 'flex', justifyContent: 'space-between' }}>
                  <span>Price / Night</span>
                  <span style={{ fontWeight: '600', color: 'var(--accent)' }}>${room.pricePerNight.toFixed(2)}</span>
                </div>

                <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                  <strong>Beds:</strong> {room.bedCount} &nbsp;|&nbsp; <strong>Amenities:</strong> {room.amenities}
                </div>
              </div>
            ))}
        </div>
      </div>
    );
  };

  const renderReservations = () => {
    return (
      <div className="reservations-view animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <button className="btn btn-primary" onClick={() => setShowReservationModal(true)}>
            <Plus size={16} /> New Booking
          </button>
        </div>

        <div className="glass-card">
          <div className="table-container">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>Guest Name</th>
                  <th>Room #</th>
                  <th>Check-In</th>
                  <th>Check-Out</th>
                  <th>Total Nights</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {reservations.map(res => (
                  <tr key={res.id}>
                    <td style={{ fontWeight: '600' }}>{res.customerName}</td>
                    <td><span className="badge badge-info">{res.roomNumber}</span></td>
                    <td>{res.checkInDate}</td>
                    <td>{res.checkOutDate}</td>
                    <td>{res.totalNights} nights</td>
                    <td>
                      <span className={`badge ${
                        res.status === 'CONFIRMED' ? 'badge-warning' : 
                        res.status === 'COMPLETED' ? 'badge-success' : 'badge-error'
                      }`}>
                        {res.status}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '8px' }}>
                        {res.status === 'CONFIRMED' && (
                          <>
                            <button className="btn btn-primary" style={{ padding: '6px 10px', fontSize: '0.8rem' }} onClick={() => handleCompleteBooking(res.id)}>
                              Check Out / Invoice
                            </button>
                            <button className="btn btn-danger" style={{ padding: '6px 10px', fontSize: '0.8rem' }} onClick={() => handleCancelBooking(res.id)}>
                              Cancel
                            </button>
                          </>
                        )}
                        {res.status === 'COMPLETED' && (
                          <button className="btn btn-secondary" style={{ padding: '6px 10px', fontSize: '0.8rem' }} onClick={() => handleViewInvoice(res.id)}>
                            View Invoice
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    );
  };

  const renderMenu = () => {
    return (
      <div className="menu-view animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', gap: '8px' }}>
            {['ALL', 'STARTER', 'MAIN_COURSE', 'DESSERT', 'BEVERAGE'].map(cat => (
              <button 
                key={cat}
                className={`tab-btn ${menuFilter === cat ? 'active' : ''}`}
                onClick={() => setMenuFilter(cat)}
              >
                {cat.replace('_', ' ')}
              </button>
            ))}
          </div>
          <button className="btn btn-primary" onClick={() => setShowMenuModal(true)}>
            <Plus size={16} /> Add Menu Item
          </button>
        </div>

        <div className="menu-grid">
          {menuItems
            .filter(item => menuFilter === 'ALL' || item.category === menuFilter)
            .map(item => (
              <div className="glass-card menu-item-card" key={item.id}>
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                    <span className="badge badge-info" style={{ fontSize: '0.65rem' }}>{item.category.replace('_', ' ')}</span>
                    <span className={`badge ${item.available ? 'badge-success' : 'badge-error'}`}>
                      {item.available ? 'In Stock' : 'Out of Stock'}
                    </span>
                  </div>
                  <h4 style={{ fontFamily: 'var(--font-display)', margin: '8px 0' }}>{item.name}</h4>
                  <p>{item.description}</p>
                </div>
                <div className="menu-item-footer">
                  <span className="menu-item-price">${item.price.toFixed(2)}</span>
                </div>
              </div>
            ))}
        </div>
      </div>
    );
  };

  const renderOrders = () => {
    return (
      <div className="orders-view animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <button className="btn btn-primary" onClick={() => setShowOrderModal(true)}>
            <Plus size={16} /> Place Food Order
          </button>
        </div>

        <div className="glass-card">
          <h3>Orders History</h3>
          <div className="table-container" style={{ marginTop: '16px' }}>
            <table className="custom-table">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Room / Table</th>
                  <th>Itemized Details</th>
                  <th>Total Amount</th>
                  <th>Order Time</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {orders.map(order => (
                  <tr key={order.id}>
                    <td>#{order.id}</td>
                    <td><span className="badge badge-info">{order.roomNumber}</span></td>
                    <td>
                      {order.items.map(it => (
                        <div key={it.id} style={{ fontSize: '0.85rem' }}>
                          {it.menuItemName} <span style={{ color: 'var(--text-secondary)' }}>x{it.quantity}</span>
                        </div>
                      ))}
                    </td>
                    <td style={{ fontWeight: '600' }}>${order.totalPrice.toFixed(2)}</td>
                    <td>{new Date(order.orderTime).toLocaleString()}</td>
                    <td>
                      <span className={`badge ${
                        order.status === 'PENDING' ? 'badge-warning' : 
                        order.status === 'PREPARING' ? 'badge-info' : 'badge-success'
                      }`}>
                        {order.status}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '8px' }}>
                        {order.status === 'PENDING' && (
                          <button className="btn btn-secondary" style={{ padding: '6px 10px', fontSize: '0.8rem' }} onClick={() => handleUpdateOrderStatus(order.id, 'PREPARING')}>
                            Cook
                          </button>
                        )}
                        {order.status === 'PREPARING' && (
                          <button className="btn btn-primary" style={{ padding: '6px 10px', fontSize: '0.8rem' }} onClick={() => handleUpdateOrderStatus(order.id, 'DELIVERED')}>
                            Deliver
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    );
  };

  const renderInventory = () => {
    return (
      <div className="inventory-view animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <button className="btn btn-primary" onClick={() => setShowInventoryModal(true)}>
            <Edit size={16} /> Adjust Stock Quantity
          </button>
        </div>

        <div className="glass-card">
          <h3>Ingedients & Stock Details</h3>
          <div className="table-container" style={{ marginTop: '16px' }}>
            <table className="custom-table">
              <thead>
                <tr>
                  <th>Item Name</th>
                  <th>Current Quantity</th>
                  <th>Unit</th>
                  <th>Min Level</th>
                  <th>Reorder Alert</th>
                </tr>
              </thead>
              <tbody>
                {inventory.map(item => {
                  const isLow = item.quantity <= item.minimumStockLevel;
                  return (
                    <tr key={item.id}>
                      <td style={{ fontWeight: '600' }}>{item.itemName}</td>
                      <td style={{ color: isLow ? 'var(--error)' : 'var(--text-primary)' }}>{item.quantity}</td>
                      <td>{item.unit}</td>
                      <td>{item.minimumStockLevel}</td>
                      <td>
                        <span className={`badge ${isLow ? 'badge-error' : 'badge-success'}`}>
                          {isLow ? 'REORDER NOW' : 'OK'}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>

        {/* Auto-reorder warnings */}
        <div className="glass-card" style={{ borderLeft: '4px solid var(--warning)' }}>
          <h4 style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--warning)' }}>
            <AlertTriangle size={18} /> Recommended Reorder List
          </h4>
          <ul style={{ margin: '12px 0 0 20px', fontSize: '0.9rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '6px' }}>
            {inventory.filter(item => item.quantity <= item.minimumStockLevel).length === 0 ? (
              <li>All inventory items are stocked above critical thresholds.</li>
            ) : (
              inventory
                .filter(item => item.quantity <= item.minimumStockLevel)
                .map(item => (
                  <li key={item.id}>
                    <strong>{item.itemName}</strong> is below minimum stock level ({item.quantity} {item.unit} left). Order at least {item.minimumStockLevel * 2} {item.unit}.
                  </li>
                ))
            )}
          </ul>
        </div>
      </div>
    );
  };

  const renderGenerator = () => {
    return (
      <div className="generator-view animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <h3>Data Scaling & Load Testing Tool</h3>
          <p style={{ fontSize: '0.95rem', color: 'var(--text-secondary)', lineHeight: '1.5' }}>
            Run large database scale generator directly using Spring Boot backend pipelines. Seed the system with thousands of entities (Reservations, Customers, Kitchen Orders) to validate backend performance under load.
          </p>

          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px', marginTop: '12px' }}>
            <div className="form-group" style={{ minWidth: '150px' }}>
              <label>Customers count</label>
              <input type="number" className="form-control" value={seedParams.customers} onChange={e => setSeedParams({ ...seedParams, customers: parseInt(e.target.value) || 0 })} />
            </div>
            <div className="form-group" style={{ minWidth: '150px' }}>
              <label>Reservations count</label>
              <input type="number" className="form-control" value={seedParams.reservations} onChange={e => setSeedParams({ ...seedParams, reservations: parseInt(e.target.value) || 0 })} />
            </div>
            <div className="form-group" style={{ minWidth: '150px' }}>
              <label>Orders count</label>
              <input type="number" className="form-control" value={seedParams.orders} onChange={e => setSeedParams({ ...seedParams, orders: parseInt(e.target.value) || 0 })} />
            </div>
            <div className="form-group" style={{ minWidth: '150px' }}>
              <label>Order items count</label>
              <input type="number" className="form-control" value={seedParams.items} onChange={e => setSeedParams({ ...seedParams, items: parseInt(e.target.value) || 0 })} />
            </div>
          </div>

          <div style={{ display: 'flex', gap: '12px', marginTop: '12px' }}>
            <button className="btn btn-primary" onClick={handleSeedMillions}>
              Run High Scale Generator
            </button>
            <button className="btn btn-secondary" onClick={handleSetupDatabase}>
              Reset and Seed Small Catalog
            </button>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="app-container">
      {/* Sidebar Navigation */}
      <aside className="sidebar">
        <div className="brand-section">
          <div className="brand-icon">
            <ChefHat size={22} />
          </div>
          <span className="brand-name">Aetheria Resort</span>
        </div>

        <ul className="nav-links">
          <li className={`nav-item ${activeTab === 'dashboard' ? 'active' : ''}`} onClick={() => setActiveTab('dashboard')}>
            <LayoutDashboard size={18} />
            <span>Dashboard</span>
          </li>
          <li className={`nav-item ${activeTab === 'rooms' ? 'active' : ''}`} onClick={() => setActiveTab('rooms')}>
            <Bed size={18} />
            <span>Hotel Rooms</span>
          </li>
          <li className={`nav-item ${activeTab === 'reservations' ? 'active' : ''}`} onClick={() => setActiveTab('reservations')}>
            <CalendarDays size={18} />
            <span>Reservations</span>
          </li>
          <li className={`nav-item ${activeTab === 'menu' ? 'active' : ''}`} onClick={() => setActiveTab('menu')}>
            <Utensils size={18} />
            <span>Restaurant Menu</span>
          </li>
          <li className={`nav-item ${activeTab === 'orders' ? 'active' : ''}`} onClick={() => setActiveTab('orders')}>
            <ChefHat size={18} />
            <span>Active Orders</span>
          </li>
          <li className={`nav-item ${activeTab === 'inventory' ? 'active' : ''}`} onClick={() => setActiveTab('inventory')}>
            <Warehouse size={18} />
            <span>Inventory</span>
          </li>
          <li className={`nav-item ${activeTab === 'generator' ? 'active' : ''}`} onClick={() => setActiveTab('generator')}>
            <Database size={18} />
            <span>Load Generator</span>
          </li>
        </ul>

        <div className="sidebar-footer">
          <div className="db-status">
            <div className={`status-indicator ${dbConnected ? '' : 'offline'}`}></div>
            <span>Backend Status: {dbConnected ? 'Online' : 'Offline'}</span>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="main-content">
        <header className="header-section">
          <div className="header-title">
            <h1 style={{ textTransform: 'capitalize' }}>{activeTab.replace('_', ' ')} Management</h1>
            <p>Welcome to Aetheria Resort full-stack operations control board.</p>
          </div>
          <button className="btn btn-secondary" onClick={fetchAllData} disabled={loading}>
            Refresh
          </button>
        </header>

        {error && (
          <div className="glass-card" style={{ borderLeft: '4px solid var(--error)', color: 'var(--error)', display: 'flex', gap: '8px', alignItems: 'center' }}>
            <AlertTriangle size={18} /> {error}
          </div>
        )}

        {loading && (
          <div style={{ display: 'flex', justifyContent: 'center', padding: '40px' }}>
            <div style={{ width: '40px', height: '40px', border: '3px solid rgba(255,255,255,0.1)', borderTopColor: 'var(--accent)', borderRadius: '50%', animation: 'spin 1s linear infinite' }}></div>
          </div>
        )}

        {/* Tab views rendering */}
        {!loading && activeTab === 'dashboard' && renderDashboard()}
        {!loading && activeTab === 'rooms' && renderRooms()}
        {!loading && activeTab === 'reservations' && renderReservations()}
        {!loading && activeTab === 'menu' && renderMenu()}
        {!loading && activeTab === 'orders' && renderOrders()}
        {!loading && activeTab === 'inventory' && renderInventory()}
        {!loading && activeTab === 'generator' && renderGenerator()}
      </main>

      {/* MODALS SECTION */}
      
      {/* 1. Add Room Modal */}
      {showRoomModal && (
        <div className="modal-overlay">
          <div className="glass-card modal-content">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
              <h3>Add New Hotel Room</h3>
              <X size={20} style={{ cursor: 'pointer' }} onClick={() => setShowRoomModal(false)} />
            </div>
            <form onSubmit={handleCreateRoom}>
              <div className="form-group">
                <label>Room Number</label>
                <input type="text" required className="form-control" placeholder="e.g. 101" value={newRoom.roomNumber} onChange={e => setNewRoom({ ...newRoom, roomNumber: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Room Type</label>
                <select className="form-control" value={newRoom.roomType} onChange={e => setNewRoom({ ...newRoom, roomType: e.target.value })}>
                  <option value="STANDARD">STANDARD</option>
                  <option value="DELUXE">DELUXE</option>
                  <option value="SUITE">SUITE</option>
                </select>
              </div>
              <div className="form-group">
                <label>Price Per Night ($)</label>
                <input type="number" required className="form-control" placeholder="e.g. 150" value={newRoom.pricePerNight} onChange={e => setNewRoom({ ...newRoom, pricePerNight: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Bed Count</label>
                <input type="number" required className="form-control" min="1" value={newRoom.bedCount} onChange={e => setNewRoom({ ...newRoom, bedCount: parseInt(e.target.value) || 1 })} />
              </div>
              <div className="form-group">
                <label>Amenities</label>
                <input type="text" className="form-control" placeholder="WiFi, Minibar, Smart TV" value={newRoom.amenities} onChange={e => setNewRoom({ ...newRoom, amenities: e.target.value })} />
              </div>
              <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                <button type="submit" className="btn btn-primary" style={{ flexGrow: 1 }}>Save Room</button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowRoomModal(false)}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 2. New Booking Modal */}
      {showReservationModal && (
        <div className="modal-overlay">
          <div className="glass-card modal-content">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
              <h3>Book a Room</h3>
              <X size={20} style={{ cursor: 'pointer' }} onClick={() => setShowReservationModal(false)} />
            </div>
            <form onSubmit={handleCreateReservation}>
              <div className="form-group">
                <label>Guest Name</label>
                <input type="text" required className="form-control" placeholder="John Doe" value={newReservation.customerName} onChange={e => setNewReservation({ ...newReservation, customerName: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Select Room Number</label>
                <select required className="form-control" value={newReservation.roomNumber} onChange={e => setNewReservation({ ...newReservation, roomNumber: e.target.value })}>
                  <option value="">-- Select Room --</option>
                  {rooms.map(room => (
                    <option key={room.id} value={room.roomNumber}>
                      Room {room.roomNumber} - {room.roomType} (${room.pricePerNight.toFixed(2)}) [{room.status}]
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Check-In Date</label>
                <input type="date" required className="form-control" value={newReservation.checkInDate} onChange={e => setNewReservation({ ...newReservation, checkInDate: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Check-Out Date</label>
                <input type="date" required className="form-control" value={newReservation.checkOutDate} onChange={e => setNewReservation({ ...newReservation, checkOutDate: e.target.value })} />
              </div>
              <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                <button type="submit" className="btn btn-primary" style={{ flexGrow: 1 }}>Confirm Booking</button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowReservationModal(false)}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 3. Add Menu Item Modal */}
      {showMenuModal && (
        <div className="modal-overlay">
          <div className="glass-card modal-content">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
              <h3>Add Food/Drink to Menu</h3>
              <X size={20} style={{ cursor: 'pointer' }} onClick={() => setShowMenuModal(false)} />
            </div>
            <form onSubmit={handleCreateMenuItem}>
              <div className="form-group">
                <label>Item Name</label>
                <input type="text" required className="form-control" placeholder="e.g. Ribeye Steak" value={newMenuItem.name} onChange={e => setNewMenuItem({ ...newMenuItem, name: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Category</label>
                <select className="form-control" value={newMenuItem.category} onChange={e => setNewMenuItem({ ...newMenuItem, category: e.target.value })}>
                  <option value="STARTER">Starter</option>
                  <option value="MAIN_COURSE">Main Course</option>
                  <option value="DESSERT">Dessert</option>
                  <option value="BEVERAGE">Beverage</option>
                </select>
              </div>
              <div className="form-group">
                <label>Price ($)</label>
                <input type="number" step="0.01" required className="form-control" placeholder="e.g. 24.99" value={newMenuItem.price} onChange={e => setNewMenuItem({ ...newMenuItem, price: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea className="form-control" rows="3" placeholder="Description of the item" value={newMenuItem.description} onChange={e => setNewMenuItem({ ...newMenuItem, description: e.target.value })}></textarea>
              </div>
              <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                <button type="submit" className="btn btn-primary" style={{ flexGrow: 1 }}>Save Item</button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowMenuModal(false)}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 4. Place Kitchen / Room Service Order Modal */}
      {showOrderModal && (
        <div className="modal-overlay">
          <div className="glass-card modal-content">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
              <h3>Place Kitchen Order</h3>
              <X size={20} style={{ cursor: 'pointer' }} onClick={() => setShowOrderModal(false)} />
            </div>
            <form onSubmit={handleCreateOrder}>
              <div className="form-group">
                <label>Active Occupied Room</label>
                <select required className="form-control" value={newOrder.roomNumber} onChange={e => setNewOrder({ ...newOrder, roomNumber: e.target.value })}>
                  <option value="">-- Select Room --</option>
                  {rooms.filter(r => r.status === 'OCCUPIED').map(room => (
                    <option key={room.id} value={room.roomNumber}>Room {room.roomNumber}</option>
                  ))}
                </select>
              </div>
              
              <div className="form-group">
                <label>Select Menu Items (Click multiple times for quantities)</label>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', maxHeight: '180px', overflowY: 'auto', padding: '10px 0' }}>
                  {menuItems.map(item => (
                    <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 8px', background: 'rgba(255,255,255,0.02)', borderRadius: '6px' }}>
                      <span style={{ fontSize: '0.9rem' }}>{item.name} (${item.price.toFixed(2)})</span>
                      <button type="button" className="btn btn-secondary" style={{ padding: '4px 8px', fontSize: '0.8rem' }} onClick={() => setNewOrder({ ...newOrder, itemIds: [...newOrder.itemIds, item.id] })}>
                        + Add
                      </button>
                    </div>
                  ))}
                </div>
              </div>

              {newOrder.itemIds.length > 0 && (
                <div style={{ background: 'rgba(99, 102, 241, 0.1)', padding: '12px', borderRadius: '8px', margin: '12px 0', fontSize: '0.9rem' }}>
                  <strong>Selected Basket:</strong>
                  <ul style={{ margin: '8px 0 0 16px' }}>
                    {Object.entries(
                      newOrder.itemIds.reduce((acc, id) => {
                        const it = menuItems.find(m => m.id === id);
                        if (it) acc[it.name] = (acc[it.name] || 0) + 1;
                        return acc;
                      }, {})
                    ).map(([name, count]) => (
                      <li key={name}>{name} x{count}</li>
                    ))}
                  </ul>
                  <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '8px', cursor: 'pointer', color: 'var(--error)' }} onClick={() => setNewOrder({ ...newOrder, itemIds: [] })}>
                    Clear Basket
                  </div>
                </div>
              )}

              <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                <button type="submit" className="btn btn-primary" style={{ flexGrow: 1 }}>Submit Order</button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowOrderModal(false)}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 5. Adjust Stock Modal */}
      {showInventoryModal && (
        <div className="modal-overlay">
          <div className="glass-card modal-content">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
              <h3>Adjust Stock Quantity</h3>
              <X size={20} style={{ cursor: 'pointer' }} onClick={() => setShowInventoryModal(false)} />
            </div>
            <form onSubmit={handleUpdateInventory}>
              <div className="form-group">
                <label>Select Inventory Ingredient</label>
                <select required className="form-control" value={inventoryUpdate.itemId} onChange={e => setInventoryUpdate({ ...inventoryUpdate, itemId: e.target.value })}>
                  <option value="">-- Select Ingredient --</option>
                  {inventory.map(item => (
                    <option key={item.id} value={item.id}>{item.itemName} ({item.quantity} {item.unit} left)</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Quantity Change (positive to restock, negative to deduct)</label>
                <input type="number" required step="0.01" className="form-control" placeholder="e.g. 50 or -10" value={inventoryUpdate.quantityChange} onChange={e => setInventoryUpdate({ ...inventoryUpdate, quantityChange: e.target.value })} />
              </div>
              <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                <button type="submit" className="btn btn-primary" style={{ flexGrow: 1 }}>Apply Adjustments</button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowInventoryModal(false)}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 6. Invoice / Billing Checkout Modal */}
      {showInvoiceModal && selectedInvoice && (
        <div className="modal-overlay">
          <div className="glass-card modal-content" style={{ maxWidth: '650px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
              <h3 style={{ fontFamily: 'var(--font-display)', fontSize: '1.4rem' }}>Aetheria Invoice - checkout complete</h3>
              <X size={20} style={{ cursor: 'pointer' }} onClick={() => setShowInvoiceModal(false)} />
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', background: 'rgba(255,255,255,0.02)', padding: '12px', borderRadius: '8px' }}>
                <div>
                  <strong>Guest Name:</strong> {selectedInvoice.customerName}<br/>
                  <strong>Room Number:</strong> {selectedInvoice.roomNumber} ({selectedInvoice.roomType})
                </div>
                <div style={{ textAlign: 'right' }}>
                  <strong>Nights:</strong> {selectedInvoice.totalNights}<br/>
                  <strong>Dates:</strong> {selectedInvoice.checkInDate} to {selectedInvoice.checkOutDate}
                </div>
              </div>

              <div>
                <h4 style={{ margin: '8px 0', fontSize: '0.95rem', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '4px' }}>Itemized Room Charges</h4>
                <div style={{ display: 'flex', justifyContent: 'space-between', padding: '4px 0' }}>
                  <span>{selectedInvoice.totalNights} Night(s) at ${selectedInvoice.pricePerNight.toFixed(2)}/night</span>
                  <strong>${selectedInvoice.roomTotalCharge.toFixed(2)}</strong>
                </div>
              </div>

              <div>
                <h4 style={{ margin: '8px 0', fontSize: '0.95rem', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '4px' }}>Room Service & Restaurant Orders</h4>
                {selectedInvoice.orders && selectedInvoice.orders.length > 0 ? (
                  selectedInvoice.orders.map(order => (
                    <div key={order.id} style={{ display: 'flex', flexDirection: 'column', gap: '4px', marginBottom: '8px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: '500' }}>
                        <span>Order #{order.id} ({new Date(order.orderTime).toLocaleDateString()})</span>
                        <span>${order.totalPrice.toFixed(2)}</span>
                      </div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', paddingLeft: '12px' }}>
                        {order.items.map(it => `${it.menuItemName} x${it.quantity}`).join(', ')}
                      </div>
                    </div>
                  ))
                ) : (
                  <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No additional restaurant orders charged to room.</div>
                )}
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', padding: '16px 0', borderTop: '1px solid var(--accent)', borderBottom: '1px solid var(--accent)', marginTop: '12px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span>Subtotal</span>
                  <span>${(selectedInvoice.roomTotalCharge + (selectedInvoice.orders ? selectedInvoice.orders.reduce((sum, o) => sum + o.totalPrice, 0) : 0)).toFixed(2)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '1.25rem', fontWeight: '700', fontFamily: 'var(--font-display)', color: 'var(--success)' }}>
                  <span>Total Due (Paid)</span>
                  <span>${selectedInvoice.finalInvoiceAmount.toFixed(2)}</span>
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '10px' }}>
                <button className="btn btn-primary" onClick={() => setShowInvoiceModal(false)}>Close & Complete</button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Extra keyframe for loading spin */}
      <style>{`
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
}
