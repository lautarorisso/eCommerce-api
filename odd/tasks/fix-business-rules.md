## eCommerce API — Mitigación de reglas de negocio y soft delete

### Specs

S1 (README): "No stock reservation between cart and payment — Stock is validated when adding items to the cart and when creating an order, but it is only actually deducted when the order is paid (`payOrder`). Between order creation and payment, other users could purchase the same stock."
S2 (README): "Cancel only available for PENDING orders — Orders can only be cancelled while in PENDING status, before payment and stock deduction. Once paid, the order must follow the full lifecycle (shipped → delivered). This is a direct consequence of the stock deduction model above."
S3 (README): "Hard deletes — Users, products, and categories are physically deleted from the database rather than soft-deleted. This is appropriate for a portfolio project where historical data retention is not a requirement."
S4 (Identificación adicional): CartItemEntity guarda `unitPrice` snapshot al añadir al carrito, pero `createOrder()` no revalida contra el precio actual del producto al crear la orden.
S5 (Identificación adicional): `deleteProduct()` valida `existsByProductIdAndActiveCart` (solo carritos activos) y `existsByProductId` (todas las órdenes incluidas CANCELLED). Hueco: productos en carritos ABANDONED se pueden borrar; productos en órdenes CANCELLED también se bloquean.
S6 (Identificación adicional): `cancelOrder()` no restaura stock (porque nunca se descontó en createOrder, pero si se cambia la lógica esto se rompe).
S7 (Identificación adicional): `deleteUser()`, `deleteCategory()`, `deleteProduct()` usan hard delete.

### Tasks

T1 — P0-1 / P1-4 — S2, S6 — DONE
T2 — P0-2 — S4 — DONE
T3 — P1-5 — S5 — DONE
T4 — P1-2 / P12 / P1-5 — S3, S7 — DONE (sin endpoint de restore; ver L4)
T5 — P1-6 — DONE
T6 — P1-7 — DONE (sin email de recordatorio)
T7 — P0-3 — DONE
T8 — P11 — DONE

### Log

L1: Usuario pide revisar el proyecto y explicar qué hace (respuesta dada)
L2: Usuario pide identificar limitaciones adicionales en reglas de negocio (respuesta dada con 10 puntos)
L3: Usuario pide plan completo de mitigación + tests primero + archivo ODD. Decisiones: soft delete para P1-5/P12; carrito @OneToOne se mantiene; carrito abandonado: solo endpoint de recover (no email).
L4: Implementación T1-T8 completada con tests primero (RED observado como fallo de compilación de tests; GREEN final `./mvnw test` → 90 tests, 0 failures, BUILD SUCCESS). Correcciones de rumbo durante T3/T4: `@SQLRestriction` en lugar de `@Where` (Hibernate 6+), se añadió `existsByProductId` a CartItemRepository, los tests de borrado se migraron de `delete/deleteById` a `save`. T5: shippingAddress/trackingNumber se cargan al enviar (markAsShipped) y se persisten paidAt/shippedAt. T6: recoverCart valida acceso y rechaza carrito ya ACTIVE. T7: límites por endpoint vía rate.limit.* con defaults (login 5/60s). T8: IllegalStateException → 409. T4 quedó sin endpoint de restore (no estaba en el alcance acordado).

### Git branch

`fix/business-rules-soft-delete`